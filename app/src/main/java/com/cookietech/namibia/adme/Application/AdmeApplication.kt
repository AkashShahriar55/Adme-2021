package com.cookietech.namibia.adme.Application

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AdmeApplication: MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
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