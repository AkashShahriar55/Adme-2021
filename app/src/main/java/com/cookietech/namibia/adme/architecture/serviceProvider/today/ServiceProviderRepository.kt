package com.cookietech.namibia.adme.architecture.serviceProvider.today

import android.util.Log
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

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

    fun fetchAllServices(callback:AllServiceFetch) {
        FirebaseManager.currentUser?.apply {
             FirebaseManager.mUserRef.document(user_id).collection("data").document("service_provider").collection("services").addSnapshotListener { value, error ->
                 Log.d("database_debug", "fetchAllServices: ")
                 error?.let {
                     callback.onFetchFailed(it)
                 }

                 value?.let {
                     val services_temp = ArrayList<ServicesPOJO>()
                     for (document in it.documents){
                         val service = document.toObject(ServicesPOJO::class.java)
                         service?.let { s -> services_temp.add(s) }
                     }
                     callback.onFetchSuccess(services_temp)
                 }
             }
        }

    }
    private val SERVICE_PROVIDER_REF= "service_provider_ref"
    fun fetchAllAppointments(uid: String): Task<QuerySnapshot> {
        return FirebaseManager.mAppointmentReference.whereEqualTo(SERVICE_PROVIDER_REF,uid).get()
    }

    interface AllServiceFetch{
        fun onFetchSuccess(services:ArrayList<ServicesPOJO>)
        fun onFetchFailed(exception: Exception)
    }

}