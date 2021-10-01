package com.cookietech.namibia.adme.architecture.common

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel
import com.cookietech.namibia.adme.interfaces.ImageUploadCallback
import com.cookietech.namibia.adme.interfaces.UpdateCallback
import com.cookietech.namibia.adme.managers.FirebaseStorageManager

class CommonViewModel : ViewModel() {
    val firebaseStorageManager = FirebaseStorageManager()
    private val commonRepository= CommonRepository()
    var imageUri : Uri? = null
    var downloadImageUrl : String? = null
    var userNme: String? = null

    var commonActivityCallbacks: CommonActivityCallbacks? = null

    fun addCommonActivityCallback(commonActivityCallbacks: CommonActivityCallbacks){
        this.commonActivityCallbacks = commonActivityCallbacks
    }

    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        commonActivityCallbacks?.processActivityResult(requestCode,resultCode,data)

    }

    fun uploadImage(uri: Uri, imageUploadCallback: ImageUploadCallback) {
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

    fun updateUserData(userNme: String?, downloadImageUrl: String?, updateCallback: UpdateCallback) {
        commonRepository.updateUserData(userNme,downloadImageUrl)
            ?.addOnSuccessListener {
                Log.d("info_update", "updateUserData: success")
                updateCallback.onUpdateSuccessFul()

            }
            ?.addOnFailureListener {
                Log.d("info_update", "updateUserData: failed: ${it.message}")
                updateCallback.onUpdateFailed()
            }

    }

    interface CommonActivityCallbacks{
        fun processActivityResult(requestCode: Int,resultCode: Int,data: Intent?)
    }
}