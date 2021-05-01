package com.cookietech.namibia.adme.architecture.serviceProvider.today

import android.net.Uri
import android.util.Log
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.google.android.gms.tasks.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.lang.Exception
import java.util.ArrayList

class AddServiceRepository {


    init {
        Log.d("service_debug", ": ")
    }

    fun fetchCategories(): Task<QuerySnapshot> {
        return FirebaseManager.mCategoryReference.get()
    }

     fun uploadImagesToServer(uri:Uri,callback: UploadImageCallback) {
        val image: StorageReference = FirebaseManager.mPortfolioImageReference.child(uri.lastPathSegment!!)
        val uploadTask = image.putFile(uri)
        uploadTask.continueWithTask<Uri>(
            Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?> { task ->
                if (!task.isSuccessful) {
                    callback.onUploadFailed(task.exception)
                }

                // Continue with the task to get the download URL
                image.downloadUrl
            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback.onUploadSuccessful(downloadUri.toString())
                Log.d("akash_debug", "onComplete: $downloadUri")
            } else {
                // Handle failures
                // ...
                callback.onUploadFailed(task.exception)
            }
        })
        uploadTask.addOnFailureListener(OnFailureListener {
            callback.onUploadFailed(it)
        }).addOnSuccessListener(
            OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                taskSnapshot.uploadSessionUri
                Log.d("akash_debug", "onSuccess: " + taskSnapshot.uploadSessionUri)

            })
            .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot -> //long percent = (taskSnapshot.getBytesTransferred()/ finalTotal_size) * 100;
                val mb = String.format("%.2f", taskSnapshot.bytesTransferred / 125000.0)
                callback.onProgressUpdate(mb)
            })
    }

    fun updateDatabase(service: ServicesPOJO): Task<DocumentReference>? {
        FirebaseManager.currentUser?.apply {

            service.user_name = user_name
            service.user_ref = user_id
            service.pic_url = profile_image_url
            service.latitude = lattitude
            service.longitude = longitude
            service.status = status_is_online
            return FirebaseManager.mUserRef.document(this.user_id).collection("data").document("service_provider").collection("services").add(service)
        }
        return null
    }

    fun updateSubServices(value: ArrayList<SubServicesPOJO>, id: String): Task<Void>? {
        FirebaseManager.currentUser?.apply {
            val reference = FirebaseManager.mUserRef.document(user_id).collection("data").document("service_provider").collection("services").document(id).collection("sub_services")
            return FirebaseManager.mDataBase.runBatch {
                for (service in value){
                    val document = reference.document()
                    service.id = document.id
                    document.set(service)
                }
            }
        }

        return null
    }

    fun getSubServices(mServiceId: String): Task<QuerySnapshot>? {
        FirebaseManager.currentUser?.apply {
            return FirebaseManager.mUserRef.document(user_id).collection("data")
                .document("service_provider").collection("services").document(mServiceId)
                .collection("sub_services").get()
        }
        return  null

    }


    interface UploadImageCallback{
        fun onProgressUpdate(mb:String)
        fun onUploadFailed(exception: Exception?)
        fun onUploadSuccessful(url:String)
    }


}