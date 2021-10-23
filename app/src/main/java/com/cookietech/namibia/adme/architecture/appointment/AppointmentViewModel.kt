package com.cookietech.namibia.adme.architecture.appointment

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ReviewPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class AppointmentViewModel:ViewModel() {
    val repository = AppointmentRepository()
    val observableServices = MutableLiveData<ArrayList<SubServicesPOJO>>()
    val observableAppointment = MutableLiveData<AppointmentPOJO>()
    val observableFinalServices = MutableLiveData<ArrayList<SubServicesPOJO>>()
    var minimumDiscount = 0.0f


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

    fun cancelAppointmentFromClient(appointment: AppointmentPOJO): Task<Void>{
        return repository.updateAppointment(appointment)
    }

    fun sendInvoiceAndFinish(appointment: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointment)
    }

    fun paymentRecieved(appointment: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointment)
    }

    fun completePayment(appointment: AppointmentPOJO):  Task<Void>  {
        return repository.updateAppointment(appointment)
    }

    fun reviewService(review: ReviewPOJO): Task<DocumentReference> {
        return repository.reviewService(review)
    }

    fun setReviewLinkInAppointment(appointment: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointment)
    }

    fun fetchReviewData(providerRef:String,serviceRef:String,reviewRef:String): Task<DocumentSnapshot> {
        return repository.fetchReviewData(providerRef, serviceRef, reviewRef)
    }

    fun declineServiceProviderResponse(appointmentPOJO: AppointmentPOJO): Task<Void> {
        return repository.updateAppointment(appointmentPOJO)
    }

    fun declineServiceProviderWorkCompletion(appointmentPOJO: AppointmentPOJO):  Task<Void> {
        return repository.updateAppointment(appointmentPOJO)
    }
}