package com.lory.library.firebaselib

/**
 * Class to hold the data received from the firebase server
 */
class FirebaseResponse<MKR> {

    /**
     * Return true is firebase response is successful
     */
    val isSuccess: Boolean

    /**
     * Return the error code occur at the time of fetching the firebase data
     */
    val errorCode: Int

    /**
     * Return the error message occur at the time of fetching the firebase data
     */
    val errorMessage: String

    /**
     * Data received from the firebase
     */
    val firebaseData: MKR?

    /**
     * Constructor called when error response is received
     * @param errorCode
     * @param errorMessage
     */
    constructor(errorCode: Int, errorMessage: String) {
        isSuccess = false
        this.errorCode = errorCode
        this.errorMessage = errorMessage
        this.firebaseData = null
    }

    /**
     * Constructor called when success response is received
     * @param firebaseData
     */
    constructor(firebaseData: MKR?) {
        isSuccess = firebaseData != null
        this.errorCode = -1
        this.errorMessage = firebaseData?.toString() ?: "No data received"
        this.firebaseData = firebaseData
    }
}