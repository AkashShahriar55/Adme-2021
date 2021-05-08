package com.cookietech.namibia.adme.architecture.appointment

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.lang.Exception

class AppointmentRepository {


    fun fetchAppointmentServices(appointment_id:String): Task<QuerySnapshot> {
        return FirebaseManager.mAppointmentReference.document(appointment_id).collection("sub_services").get()
    }

    fun updateAppointment(appointment: AppointmentPOJO): Task<Void> {
        return FirebaseManager.mAppointmentReference.document(appointment.id!!).set(appointment)
    }

    fun uploadInvoice(name:String,invoiceBitmap:Bitmap,callback: UploadInvoiceCallback){
        val image: StorageReference = FirebaseManager.mInvoiceImageReference.child(name)
        val baos = ByteArrayOutputStream()
        invoiceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var uploadTask = image.putBytes(data)
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

    interface UploadInvoiceCallback{
        fun onProgressUpdate(mb:String)
        fun onUploadFailed(exception: Exception?)
        fun onUploadSuccessful(url:String)
    }
}