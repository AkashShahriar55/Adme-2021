package com.cookietech.namibia.adme.architecture.common

import com.cookietech.namibia.adme.managers.FirebaseManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions

class CommonRepository {
    fun updateUserData(userNme: String?, downloadImageUrl: String?): Task<Void>? {
        val documentData : HashMap<String,String> = HashMap()

        userNme?.let {
            documentData.set("user_name", it.trim())
        }
        downloadImageUrl?.let {
            documentData.set("profile_image_url", it.trim())
        }

        if (!documentData.isEmpty()){
            FirebaseManager.currentUser?.let {
                return FirebaseManager.mUserRef.document(it.user_id).set(documentData, SetOptions.merge())
            }
        }
        return null
    }
}