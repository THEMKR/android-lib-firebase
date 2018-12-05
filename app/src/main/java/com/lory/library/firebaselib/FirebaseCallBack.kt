package com.lory.library.firebaselib

/**
 * Created by A1ZFKXA3 on 1/30/2018.
 */
interface FirebaseCallBack<MKR> {
    /**
     * Method to notify that the data is successfully fetch from the Firebase
     *
     * @param mkr
     */
    fun onFirebaseSuccess(mkr: MKR?)

    /**
     * Method to notify that the data is failed to fetch from the Firebase
     *
     * @param errorCode
     * @param errorMessage
     */
    fun onFirebaseFailed(errorCode: Int, errorMessage: String)
}
