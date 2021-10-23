package com.cookietech.namibia.adme.extensions

import android.content.Context
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat


fun Context.openNetworkSetting(){
    val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
    this.startActivity(intent)
}