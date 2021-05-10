package com.cookietech.namibia.adme.architecture.appointment

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task

class AppointmentViewModel:ViewModel() {
    val repository = AppointmentRepository()
    val observableServices = MutableLiveData<ArrayList<SubServicesPOJO>>()
    val observableAppointment = MutableLiveData<AppointmentPOJO>()
    val observableFinalServices = MutableLiveData<ArrayList<SubServicesPOJO>>()


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

    fun fetchAppointment(appointment_id:String){
        repository.fetchAppointment(appointment_id).addOnSuccessListener { document ->
            if (document != null) {
                val appointmentPOJO =  document.toObject(AppointmentPOJO::class.java)
                appointmentPOJO?.id = document.id
                observableAppointment.value = appointmentPOJO
            } else {
            Log.d("appointment_debug", "No such document")
        }

        }.addOnFailureListener {

        }
    }

    fun sendServiceProviderResponse(appointment: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointment)
    }

    fun uploadInvoiceToStorage(name:String,invoice:Bitmap,callback: AppointmentRepository.UploadInvoiceCallback){
        return repository.uploadInvoice(name,invoice,callback)
    }


    fun approveServiceProviderResponse(appointment: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointment)
    }

    fun sendWorkCompleted(appointmentPOJO: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointmentPOJO)
    }

    fun approveProviderWorkCompletion(appointmentPOJO: AppointmentPOJO):  Task<Void> {
        return repository.updateAppointment(appointmentPOJO)
    }

    fun sendInvoiceAndFinish(appointment: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointment)
    }
}