package com.cookietech.namibia.adme.architecture.client.myDeals

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.today.ServiceProviderRepository
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent

class MyDealsViewModel:ViewModel() {

    val repository = MyDealsRepository()
    val observableAppointments = SingleLiveEvent<ArrayList<AppointmentPOJO>>()


    init {
        fetchAllAppointments()
    }

    private fun fetchAllAppointments() {
        FirebaseManager.mFirebaseUser?.apply {
            repository.fetchAllAppointments(uid).addOnSuccessListener { documents->
                if(documents.isEmpty){

                }else{
                    val appointments = ArrayList<AppointmentPOJO>()
                    for (document in documents){
                        val appointment = document.toObject(AppointmentPOJO::class.java)
                        appointment.id = document.id
                        appointments.add(appointment)
                    }
                    observableAppointments.value = appointments
                }

            }.addOnFailureListener {

            }
        }
    }

}