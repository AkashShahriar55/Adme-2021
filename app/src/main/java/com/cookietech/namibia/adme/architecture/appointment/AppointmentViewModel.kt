package com.cookietech.namibia.adme.architecture.appointment

import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent

class AppointmentViewModel:ViewModel() {
    val repository = AppointmentRepository()
    val observableServices = SingleLiveEvent<ArrayList<SubServicesPOJO>>()



    fun fetchAppointmentServices(appointment_id:String){
        repository.fetchAppointmentServices(appointment_id).addOnSuccessListener {
            if(!it.isEmpty){
                val temp_services = ArrayList<SubServicesPOJO>()
                for (document in it){
                    val service = document.toObject(SubServicesPOJO::class.java)
                    temp_services.add(service)
                }
                observableServices.value = temp_services
            }
        }.addOnFailureListener {

        }
    }
}