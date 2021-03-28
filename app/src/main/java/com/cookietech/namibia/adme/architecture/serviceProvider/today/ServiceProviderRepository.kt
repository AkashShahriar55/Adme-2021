package com.cookietech.namibia.adme.architecture.serviceProvider.today

import android.util.Log
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.models.UserPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class ServiceProviderRepository {
    private val SERVICE_PROVIDER_PATH = "/data/service_provider"
    fun createOrFetchServiceProvider(user_id: String, callback: ServiceProviderDataCallback,data:SingleLiveEvent<ServiceProviderPOJO?>) {
        val service_provider_reference = FirebaseManager.mUserRef.document(user_id+SERVICE_PROVIDER_PATH)
        service_provider_reference.get().addOnCompleteListener { task->
            if (task.isSuccessful) {
                Log.d("user_creation", "createUser: successful")
                val document: DocumentSnapshot = task.getResult()
                if (document.exists()) {
                    val value = document.toObject(ServiceProviderPOJO::class.java)
                    value?.let {
                        data.value = value
                        callback.onFetchSuccessful()
                    }


                } else {

                    val new_user = ServiceProviderPOJO()
                    service_provider_reference.set(new_user).addOnSuccessListener {
                        data.value = new_user
                        callback.onCreateSuccessful()
                    }.addOnFailureListener {
                        Log.d("user_creation", "createUser: " + it.message)
                        callback.onCreateOrFetchFailed(it)
                    }
                }
            } else {
                task.exception?.let { callback.onCreateOrFetchFailed(it) }
            }
        }

    }
}