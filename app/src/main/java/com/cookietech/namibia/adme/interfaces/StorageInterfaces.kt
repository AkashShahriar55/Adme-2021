package com.cookietech.namibia.adme.interfaces

interface ImageUploadCallback{
    fun onImageUploaded(url:String)
    fun onImageUploadFailed()
}