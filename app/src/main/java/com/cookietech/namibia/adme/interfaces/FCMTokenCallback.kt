package com.cookietech.namibia.adme.interfaces

interface FCMTokenCallback {
    fun onTokenGenerationSuccess(token : String)
    fun onTokenGenerationFailed()
}