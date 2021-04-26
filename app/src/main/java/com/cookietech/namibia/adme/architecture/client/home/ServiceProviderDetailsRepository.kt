package com.cookietech.namibia.adme.architecture.client.home

import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.ArrayList

class ServiceProviderDetailsRepository {

    fun fetchServiceProviderSubServices(user_ref:String,service_id:String): Task<QuerySnapshot> {
        return FirebaseManager.mUserRef.document(user_ref).collection("data")
            .document("service_provider").collection("services").document(service_id)
            .collection("sub_services").get()
    }

    fun fetchFullServiceDetails(user_ref:String,service_id:String): Task<DocumentSnapshot> {
        return FirebaseManager.mUserRef.document(user_ref).collection("data")
            .document("service_provider").collection("services").document(service_id)
            .get()
    }

    fun sendRequest(appointment: AppointmentPOJO): Task<DocumentReference> {
        return FirebaseManager.mAppointmentReference.add(appointment)
    }

    fun addSubServicesToAppointment(selectedServices: ArrayList<SubServicesPOJO>, id: String): Task<Void> {
        val reference = FirebaseManager.mAppointmentReference.document(id).collection("sub_services")
        return  FirebaseManager.mDataBase.runBatch {
            for (service in selectedServices){
                val document = reference.document(service.id!!)
                document.set(service)
            }
        }
    }

    fun removeRequestForFailure(id: String) {
        FirebaseManager.mAppointmentReference.document(id).delete()
    }
}