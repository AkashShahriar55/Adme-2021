package com.cookietech.namibia.adme.architecture.appointment

import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class AppointmentRepository {


    fun fetchAppointmentServices(appointment_id:String): Task<QuerySnapshot> {
        return FirebaseManager.mAppointmentReference.document(appointment_id).collection("sub_services").get()
    }

    fun updateAppointment(appointment: AppointmentPOJO): Task<Void> {
        return FirebaseManager.mAppointmentReference.document(appointment.id!!).set(appointment)
    }
}