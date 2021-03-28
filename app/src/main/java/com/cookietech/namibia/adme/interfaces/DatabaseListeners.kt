package com.cookietech.namibia.adme.interfaces

import java.lang.Exception

interface UpdateDataCallback{
    fun updateSuccessful()
    fun updateFailed()
}


interface ServiceProviderDataCallback{
    fun onCreateSuccessful()
    fun onFetchSuccessful()
    fun onCreateOrFetchFailed(exception: Exception)
}