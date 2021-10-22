package com.cookietech.namibia.adme.architecture.client.myDeals

import com.cookietech.namibia.adme.managers.FirebaseManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class MyDealsRepository {
    private val CLIENT_PROVIDER_REF= "client_ref"
    fun fetchAllAppointments(uid: String): Query {
        return FirebaseManager.mAppointmentReference.whereEqualTo(CLIENT_PROVIDER_REF,uid).orderBy("time_in_millis",Query.Direction.DESCENDING)
    }
}