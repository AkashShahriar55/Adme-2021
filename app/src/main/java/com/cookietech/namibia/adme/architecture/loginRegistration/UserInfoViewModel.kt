package com.cookietech.namibia.adme.architecture.loginRegistration

import android.app.Activity
import android.app.Application
import android.location.Location
import android.net.Uri
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cookietech.namibia.adme.interfaces.ImageUploadCallback
import com.cookietech.namibia.adme.interfaces.UpdateDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.FirebaseStorageManager
import com.cookietech.namibia.adme.managers.LocationManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.cookietech.namibia.adme.utils.SingleLiveEvent

class UserInfoViewModel(application: Application) :AndroidViewModel(application) {

    val locationLiveData:SingleLiveEvent<Location?> = SingleLiveEvent()
    val locationManager = LocationManager()
    val firebaseStorageManager = FirebaseStorageManager()
    private val loginAndRegistrationManager = LoginAndRegistrationManager()

    fun fetchCurrentLocation(activity: Activity):MutableLiveData<Location?>{
        locationManager.fetchCurrentLocation(activity, locationLiveData)
        return locationLiveData
    }


    fun uploadImage(uri: Uri,imageUploadCallback: ImageUploadCallback){
        firebaseStorageManager.uploadImage(uri).addOnCompleteListener {task->
            if (task.isSuccessful) {
                val downloadUri: Uri = task.result
                imageUploadCallback.onImageUploaded(downloadUri.toString())
            } else {
                imageUploadCallback.onImageUploadFailed()
                // Handle failures
                // ...
            }
        }.addOnFailureListener {
            imageUploadCallback.onImageUploadFailed()
        }.addOnSuccessListener {

        }
    }

    fun updateUserInfo(
        name: String,
        phoneNumber: String,
        lat: String,
        lng: String,
        imageDownloadUrl: String?,
        updateDataCallback: UpdateDataCallback
    ) {
        FirebaseManager.currentUser?.apply {
            Log.d("update_debug", "updateUserInfo: ")
            this.user_name = name
            this.phone = phoneNumber
            this.lattitude = lat
            this.longitude = lng
            this.profile_image_url = imageDownloadUrl
            this.user_info_updated = true
            loginAndRegistrationManager.updateUserInfo(this).addOnSuccessListener {
                Log.d("update_debug", "addOnSuccessListener: ")
                updateDataCallback.updateSuccessful()
            }.addOnFailureListener {
                Log.d("update_debug", "addOnFailureListener: " + it.message)
                updateDataCallback.updateFailed()
            }
        }
    }


}