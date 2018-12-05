package com.lory.library.firebaselib

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.firebase.database.*

/**
 * Created by mkr on 3/4/18.
 */
abstract class BaseFirebaseTask<MKR> {
    protected val context: Context
    private var firebaseCallBack: FirebaseCallBack<MKR>? = null
    private var mkrWorker: MKRWorker? = null
    private var lock: Boolean = false

    /**
     * Constructor
     *
     * @param context
     * @param firebaseCallBack
     */
    constructor(context: Context, firebaseCallBack: FirebaseCallBack<MKR>?) {
        this.context = context;
        this.firebaseCallBack = firebaseCallBack;
    }

    /**
     * Method to Execute the network call.<br></br>This Method is run on back thread
     */
    fun executeTask() {
        mkrWorker = MKRWorker()
        mkrWorker?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    /**
     * Method to get the list of the path of Firebase data, where the item ArrayList is the set of Root->Child->Child->Child, Always return the path from the Root Node
     * @return List of Path which contain the List of Child->to->Child path
     */
    protected abstract fun getFirebaseDatabasePathList(): ArrayList<ArrayList<String>>

    /**
     * Method to get the Data from Firebase
     * @param dataSnapshot
     */
    protected abstract fun parseFirebaseDataSnapShot(dataSnapshot: DataSnapshot): MKR

    /**
     * Method to know weather the user call this task to get the value of multiple key or not
     * @return TRUE if fetch multiple key value else FALSE
     */
    protected abstract fun isBulkRequest(): Boolean

    /**
     * Method to get the Data from Firebase
     * @param dataSnapshot
     */
    protected abstract fun parseFirebaseDataSnapShot(dataSnapshotList: ArrayList<Any>): MKR

    /**
     * Method called in back Thread. Method to execute Firebase Api and get the Data from FireBase.
     * Overrider handel the Firebase call
     * @return DataSnapshot/DatabaseError
     */
    private fun doInBackground(): FirebaseResponse<MKR> {
        if (!ConnectivityInfoUtils.isConnected(context, true)) {
            return getNoNetworkError()
        }
        var firebaseData: FirebaseData
        var retryCount = 0
        while (true) {
            retryCount++
            firebaseData = executeFirebase()
            if (retryCount >= getMaxRetryCount() || isFirebaseDataSuccess(firebaseData.dataArray)) {
                break
            }
        }
        // CHECK CONDITIONS
        if (firebaseData != null) {
            if (!isBulkRequest()) {
                if (firebaseData.dataArray.size == 0) {
                    return getNoResponseReceivedError() // NOTHING RECEIVED FROM SERVER
                }
                return if (firebaseData.dataArray.get(0) is DataSnapshot) {
                    FirebaseResponse(parseFirebaseDataSnapShot(firebaseData.dataArray.get(0) as DataSnapshot)) // SUCCESS DATA RECEIVED FROM SERVER
                } else if (firebaseData.dataArray.get(0) is DatabaseError) {
                    parseFirebaseDataError(firebaseData.dataArray.get(0) as DatabaseError) // ERROR RECEIVED FROM SERVER
                } else {
                    getMiscellaneousError() // MIS DATA RECEIVED FROM SERVER
                }
            } else {
                return FirebaseResponse(parseFirebaseDataSnapShot(firebaseData.dataArray)) // BULK DATA RECEIVED FROM SERVER
            }
        } else {
            return getNoResponseReceivedError()
        }
    }

    /**
     * Method to execute the Actual firebase Request
     */
    private fun executeFirebase(): FirebaseData {
        lockTask()
        val firebaseDatabasePathList = getFirebaseDatabasePathList()
        var firebaseData: ArrayList<Any> = ArrayList()
        var databaseReference: DatabaseReference = if (getFirebaseDatabaseURL() != null) {
            FirebaseDatabase.getInstance(getFirebaseDatabaseURL()!!)
        } else {
            FirebaseDatabase.getInstance()
        }.reference
        val callback = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                firebaseData.add(dataSnapshot)
                if (firebaseData.size >= firebaseDatabasePathList.size) {
                    unlockTask()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                firebaseData.add(databaseError)
                if (firebaseData.size >= firebaseDatabasePathList.size) {
                    unlockTask()
                }
            }
        }
        for (path in firebaseDatabasePathList) {
            databaseReference = databaseReference.root
            for (node in path) {
                databaseReference = databaseReference.child(node)
            }
            databaseReference.addListenerForSingleValueEvent(callback)
        }
        // LOOP LOCK
        while (isTaskLock()) {
            try {
                Thread.sleep(50)
            } catch (e: Exception) {
                Log.e("MKR", "BaseFirebaseTask().executeFirebase() : ${e.message}")
            }
        }
        return FirebaseData(firebaseData!!)
    }

    /**
     * Method to get the Max Retry Count
     */
    protected fun getMaxRetryCount(): Int {
        return 3
    }

    /**
     * Method to get the Data from Firebase
     * @param databaseError Array<DataSnapshot/DatabaseError>
     */
    protected fun parseFirebaseDataError(databaseError: DatabaseError): FirebaseResponse<MKR> {
        return FirebaseResponse<MKR>(databaseError.code, databaseError.message)
    }

    /**
     * Method to get the Response when Network is not there
     */
    protected fun getNoNetworkError(): FirebaseResponse<MKR> {
        return FirebaseResponse(ERROR.NO_NETWORK_CODE, ERROR.NO_NETWORK_MESSAGE)
    }

    /**
     * Method to get the Response when Miscellaneous error occured
     */
    protected fun getMiscellaneousError(): FirebaseResponse<MKR> {
        return FirebaseResponse(ERROR.MISCELLANEOUS_ERROR_CODE, ERROR.MISCELLANEOUS_ERROR_MESSAGE)
    }

    /**
     * Method to get the Response when No response received from Firebase
     */
    protected fun getNoResponseReceivedError(): FirebaseResponse<MKR> {
        return FirebaseResponse(ERROR.NOTHING_RECEIVED_CODE, ERROR.NOTHING_RECEIVED_MESSAGE)
    }

    /**
     * Method to get the Firebase Database URL.
     * @return URL if fetch data from another project, else return NULL
     */
    protected fun getFirebaseDatabaseURL(): String? {
        return null
    }

    /**
     * Method to check weather there is at least singel success item in List
     * @param dataArray
     */
    private fun isFirebaseDataSuccess(dataArray: ArrayList<Any>): Boolean {
        for (data in dataArray) {
            if (data is DataSnapshot) {
                return true
            }
        }
        return false
    }

    /**
     * Method to Execute before the mkrWorker start background execution
     */
    protected fun preExecute() {
        // Do whatever you want to
    }

    /**
     * Method to Execute after the mkrWorker response
     */
    protected fun postExecute(mkr: MKR?): MKR? {
        return mkr
    }

    /**
     * Method to unlock the task
     */
    @Synchronized
    protected fun unlockTask() {
        lock = false
    }

    /**
     * Method to lock the task
     */
    @Synchronized
    protected fun lockTask() {
        lock = true
    }

    /**
     * Method to check weather the task is lock or not
     */
    @Synchronized
    protected fun isTaskLock(): Boolean {
        return lock
    }

    /**
     * Class helpful to working on background
     */
    private inner class MKRWorker : AsyncTask<Void, Void, FirebaseResponse<MKR>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            this@BaseFirebaseTask.preExecute()
        }

        override fun doInBackground(vararg voids: Void): FirebaseResponse<MKR> {
            return this@BaseFirebaseTask.doInBackground()
        }

        override fun onPostExecute(mkr: FirebaseResponse<MKR>) {
            super.onPostExecute(mkr)
            if (mkr.isSuccess) {
                firebaseCallBack?.onFirebaseSuccess(mkr.firebaseData)
            } else {
                firebaseCallBack?.onFirebaseFailed(mkr.errorCode, mkr.errorMessage)
            }
        }
    }

    /**
     * Class to hold the data received from Firebase
     */
    class FirebaseData {
        val dataArray: ArrayList<Any>

        /**
         * Constructor
         * @param dataArray
         */
        constructor(dataArray: ArrayList<Any>) {
            this.dataArray = dataArray
        }
    }

    interface ERROR {
        companion object {
            const val MISCELLANEOUS_ERROR_CODE = 0
            const val MISCELLANEOUS_ERROR_MESSAGE = "Miscellaneous error occur"

            const val NO_NETWORK_CODE = -1
            const val NO_NETWORK_MESSAGE = "No Network"

            const val NOTHING_RECEIVED_CODE = -2
            const val NOTHING_RECEIVED_MESSAGE = "Nothing received from the server"
        }
    }
}