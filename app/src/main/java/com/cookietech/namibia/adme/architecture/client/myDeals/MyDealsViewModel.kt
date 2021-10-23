package com.cookietech.namibia.adme.architecture.client.myDeals

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.today.ServiceProviderRepository
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot

class MyDealsViewModel:ViewModel() {

    val repository = MyDealsRepository()
    val observableAppointments = MutableLiveData<ArrayList<AppointmentPOJO>>()


    init {
        fetchAllAppointments()
    }

    private fun fetchAllAppointments() {
        FirebaseManager.mFirebaseUser?.apply {
            repository.fetchAllAppointments(uid).addSnapshotListener {documents, error ->
                Log.d("deals_debug", "fetchAllAppointments: " + documents?.isEmpty+" " + error)
                error?.let {
                    observableAppointments.value = arrayListOf()
                }

                documents?.let {
                    val appointments = ArrayList<AppointmentPOJO>()
                    for (document in documents){
                        val appointment = document.toObject(AppointmentPOJO::class.java)
                        appointment.id = document.id
                        appointments.add(appointment)
                    }
                    observableAppointments.value = appointments
                }?:run {
                    observableAppointments.value = arrayListOf()
                }
            }
        }
    }

}