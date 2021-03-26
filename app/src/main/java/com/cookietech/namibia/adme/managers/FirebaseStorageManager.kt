package com.cookietech.namibia.adme.managers

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import java.lang.NullPointerException

class FirebaseStorageManager {
    private val STORAGE_FOLDER_PROFILE_PICTURE = "profile_picture"
    fun uploadImage(uri: Uri): Task<Uri> {
        val profile_pic_ref: StorageReference =
            FirebaseManager.mStorage.reference.child(STORAGE_FOLDER_PROFILE_PICTURE)
        val image = profile_pic_ref.child(uri.lastPathSegment!!)
        val uploadTask = image.putFile(uri)

        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            // Continue with the task to get the download URL
            image.downloadUrl
        }

    }

}