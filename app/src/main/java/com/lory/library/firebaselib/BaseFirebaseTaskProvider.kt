package com.lory.library.firebaselib

/**
 * Created by A1ZFKXA3 on 1/30/2018.
 */

open class BaseFirebaseTaskProvider {
    private var mIsAttach: Boolean = false

    /**
     * Method to attach provider
     */
    fun attachProvider() {
        mIsAttach = true
    }

    /**
     * Method to detach provider
     */
    fun detachProvider() {
        mIsAttach = false
    }

    /**
     * Method to Notify Caller
     *
     * @param firebaseCallBack
     * @param o
     */
    fun notifyTaskSuccess(firebaseCallBack: FirebaseCallBack<Any>, o: Any) {
        if (mIsAttach) {
            firebaseCallBack.onFirebaseSuccess(o)
        }
    }

    /**
     * Method to Notify Caller
     *
     * @param firebaseCallBack
     * @param errorCode
     * @param errorMessage
     */
    fun notifyTaskFailed(firebaseCallBack: FirebaseCallBack<Any>, errorCode: Int, errorMessage: String) {
        if (mIsAttach) {
            firebaseCallBack.onFirebaseFailed(errorCode, errorMessage)
        }
    }
}
