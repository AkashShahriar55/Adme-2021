package com.cookietech.namibia.adme.interfaces

interface AuthConnectionCallback {
    fun onAuthConnectionSuccessful(provider: String)
    fun onAuthConnectionFailed()
}