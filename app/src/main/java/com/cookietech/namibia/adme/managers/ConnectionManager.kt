package com.cookietech.namibia.adme.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData

object ConnectionManager {
    var networkAvailability: MutableLiveData<Boolean?> = MutableLiveData()

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun getObservableNetworkAvailability(): MutableLiveData<Boolean?> {
        return networkAvailability
    }
}



class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = conn.activeNetworkInfo

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        if (networkInfo != null) {
            Log.d("akash_net_debug", "onReceive: connected")
            ConnectionManager.networkAvailability.setValue(true)
        } else {
            Log.d("akash_net_debug", "onReceive: not connected")
            ConnectionManager.networkAvailability.setValue(false)
        }
    }
}