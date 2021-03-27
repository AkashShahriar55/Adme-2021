package com.cookietech.namibia.adme.Application

import android.content.Context
import androidx.multidex.MultiDexApplication

class AdmeApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = this
    }

    companion object{
        var APP_CONTEXT:Context? = null
    }
}