package com.cookietech.namibia.adme.architecture.serviceProvider.today

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cookietech.namibia.adme.Application.Status
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.lang.Exception

class ServiceProviderRepository {
    private val SERVICE_PROVIDER_PATH = "/data/service_provider"
    private val listeners:ArrayList<ListenerRegistration>  = ArrayList();
    fun createOrFetchServiceProvider(user_id: String, callback: ServiceProviderDataCallback,data: MutableLiveData<ServiceProviderPOJO?>) {
        val service_provider_reference = FirebaseManager.mUserRef.document(user_id+SERVICE_PROVIDER_PATH)
        val listener = service_provider_reference.addSnapshotListener { document, error ->

            error?.let {
                callback.onCreateOrFetchFailed(it)
            }

            document?.let {document
                Log.d("user_creation", "createUser: successful")
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
            }


        }

        listeners.add(listener)

//        service_provider_reference.get().addOnCompleteListener { task->
//            if (task.isSuccessful) {
//                Log.d("user_creation", "createUser: successful")
//                val document: DocumentSnapshot = task.getResult()
//                if (document.exists()) {
//                    val value = document.toObject(ServiceProviderPOJO::class.java)
//                    value?.let {
//                        data.value = value
//                        callback.onFetchSuccessful()
//                    }
//
//
//                } else {
//
//                    val new_user = ServiceProviderPOJO()
//                    service_provider_reference.set(new_user).addOnSuccessListener {
//                        data.value = new_user
//                        callback.onCreateSuccessful()
//                    }.addOnFailureListener {
//                        Log.d("user_creation", "createUser: " + it.message)
//                        callback.onCreateOrFetchFailed(it)
//                    }
//                }
//            } else {
//                task.exception?.let { callback.onCreateOrFetchFailed(it) }
//            }
//        }

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
                         service?.mServiceId = document.id
                         service?.let { s -> services_temp.add(s) }
                     }
                     callback.onFetchSuccess(services_temp)
                 }
             }
        }

    }
    private val SERVICE_PROVIDER_REF= "service_provider_ref"
    fun fetchAllAppointments(
        uid: String,
        observableAppointments: MutableLiveData<ArrayList<AppointmentPOJO>>
    ){
        val listener = FirebaseManager.mAppointmentReference.orderBy("state").orderBy("time_in_millis",Query.Direction.DESCENDING).whereNotIn("state", listOf(Status.status_client_request_cancel,Status.status_payment_completed,Status.status_provider_request_cancel)).whereEqualTo(SERVICE_PROVIDER_REF,uid).addSnapshotListener { documents, error ->
            error?.let {
                Log.d("appointment_debug", "fetchAllAppointments: " + it.message)
                observableAppointments.value = ArrayList()
            }

            documents?.let { documents->
                if(documents.isEmpty){
                    Log.d("appointment_debug", "fetchAllAppointments: empty")
                    observableAppointments.value = ArrayList()
                }else{
                    val appointments = ArrayList<AppointmentPOJO>()
                    for (document in documents){
                        val appointment = document.toObject(AppointmentPOJO::class.java)
                        appointment.id = document.id
                        appointments.add(appointment)

                    }
                    observableAppointments.value = appointments
                }
            }
        }

        listeners.add(listener)

    }

    interface AllServiceFetch{
        fun onFetchSuccess(services:ArrayList<ServicesPOJO>)
        fun onFetchFailed(exception: Exception)
    }


    fun removeListeners(){
        for(listener in listeners){
            listener.remove()
        }
    }

}