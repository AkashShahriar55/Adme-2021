package com.cookietech.namibia.adme.Application

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.cookietech.namibia.adme.managers.ConnectionManager
import com.cookietech.namibia.adme.managers.NetworkReceiver
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AdmeApplication: MultiDexApplication(), LifecycleObserver {
    private var networkReceiver: NetworkReceiver? = null
    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        networkReceiver = NetworkReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(networkReceiver, filter)
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onMoveToForeground() {
        receiverId = tempReceiverId
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onMoveToBackground() {
        tempReceiverId = receiverId
        receiverId = null
    }

    companion object{
        var APP_CONTEXT:Context? = null
        var tempReceiverId: String? = null
        var receiverId: String? = null
    }
}