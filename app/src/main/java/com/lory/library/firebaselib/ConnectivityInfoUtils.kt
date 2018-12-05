package com.lory.library.firebaselib

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by A1ZFKXA3 on 8/31/2017.
 */

internal class ConnectivityInfoUtils {
    companion object {

        /**
         * Get the Information of network
         *
         * @param context
         * @return
         */
        private fun getNetworkInfo(context: Context): NetworkInfo? {
            return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        }

        /**
         * Method to check weather the device adapter is connected to the network or not.
         * <br> Only check Adapter Setting
         *
         * @param context
         * @return TRUE if connected with network, else return FALSE
         */
        private fun isConnected(context: Context): Boolean {
            val info = ConnectivityInfoUtils.getNetworkInfo(context)
            return info != null && info.isConnected
        }

        /**
         * Method to check weather the device is actually connected to the network or not.<br></br>
         * Method Will take time to response.
         *
         * @param context
         * @param onConnectivityInfoUtilsListener Callback to notifyTaskResponse the Connection state.
         */
        fun isConnected(context: Context, onConnectivityInfoUtilsListener: OnConnectivityInfoUtilsListener) {
            if (isConnected(context)) {
                object : AsyncTask<Void, Void, Boolean>() {

                    override fun doInBackground(vararg voids: Void): Boolean? {
                        var urlConnection: HttpURLConnection? = null
                        try {
                            val url = URL("https://www.google.com/")
                            urlConnection = url.openConnection() as HttpURLConnection
                            urlConnection.requestMethod = "GET"
                            urlConnection.readTimeout = 3000
                            urlConnection.connectTimeout = 3000
                            val responseCode = urlConnection.responseCode
                            when (responseCode) {
                                HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_ACCEPTED, HttpURLConnection.HTTP_CREATED, HttpURLConnection.HTTP_NO_CONTENT -> {
                                    return true
                                }
                                else -> {

                                }
                            }
                        } catch (e: Exception) {
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect()
                            }
                        }
                        return false
                    }

                    override fun onPostExecute(aBoolean: Boolean?) {
                        super.onPostExecute(aBoolean)
                        if (aBoolean != null && aBoolean!!) {
                            onConnectivityInfoUtilsListener?.onConnectivityInfoUtilsNetworkConnected()
                        } else {
                            onConnectivityInfoUtilsListener?.onConnectivityInfoUtilsNetworkDisconnected()
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            } else {
                onConnectivityInfoUtilsListener?.onConnectivityInfoUtilsNetworkDisconnected()
            }
        }

        /**
         * Method to check weather the device is actually connected to the network or not.<br>
         * If "isCheckLiveConnection = true" then it take some time to return the result.
         * @param context
         * @param isCheckLiveConnection FALSE to check weather the Adapter is connected or not, & TRUE if check check weather the Device is actually connected to network or not
         */
        fun isConnected(context: Context, isCheckLiveConnection: Boolean): Boolean {
            if (isConnected(context)) {
                if (!isCheckLiveConnection) {
                    return true
                }
                var urlConnection: HttpURLConnection? = null
                try {
                    val url = URL("https://www.google.com/")
                    urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.readTimeout = 3000
                    urlConnection.connectTimeout = 3000
                    val responseCode = urlConnection.responseCode
                    when (responseCode) {
                        HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_ACCEPTED, HttpURLConnection.HTTP_CREATED, HttpURLConnection.HTTP_NO_CONTENT -> {
                            return true
                        }
                        else -> {

                        }
                    }
                } catch (e: Exception) {
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect()
                    }
                }
                return false
            } else {
                return false
            }
        }
    }

    /**
     * Callback to listen the Network Connectivity Event
     */
    interface OnConnectivityInfoUtilsListener {

        /**
         * Method to notifyTaskResponse that user connected with network
         */
        fun onConnectivityInfoUtilsNetworkConnected()

        /**
         * Method to notifyTaskResponse that user dis-connected with network
         */
        fun onConnectivityInfoUtilsNetworkDisconnected()
    }
}
