package com.cookietech.namibia.adme.architecture.client.home

import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.models.UserPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import java.lang.Exception

class ServiceProviderDetailsViewModel: ViewModel() {
    val repository = ServiceProviderDetailsRepository()
    val observableSubServices = SingleLiveEvent<ArrayList<SubServicesPOJO>>()
    val observableServiceFullDetails = SingleLiveEvent<ServicesPOJO>()
    val observableServiceProviderUserInfo = SingleLiveEvent<UserPOJO>()

    fun fetchSubServices(user_id:String,service_id:String){
        repository.fetchServiceProviderSubServices(user_id,service_id).addOnCompleteListener {
            if(it.isSuccessful){
                val subServices = arrayListOf<SubServicesPOJO>()
                for(document in it.result.documents){
                    val subService = document.toObject(SubServicesPOJO::class.java)
                    subService?.quantity = 0
                    subService?.id = document.id
                    subService?.let { sService -> subServices.add(sService) }
                }
                observableSubServices.value = subServices
            }else{

            }
        }.addOnFailureListener {

        }
    }

    fun fetchFullServiceDetails(user_id:String,service_id:String) {
        repository.fetchFullServiceDetails(user_id,service_id).addOnSuccessListener {document->
            val subService = document.toObject(ServicesPOJO::class.java)
            subService?.mServiceId = document.id
            subService?.let {
                observableServiceFullDetails.value = it
            }
        }.addOnFailureListener {

        }
    }

    fun fetchPhoneNumber(userID: String) {
        FirebaseManager.mUserRef.document(userID).get().addOnSuccessListener {document->
            val user_data = document.toObject(UserPOJO::class.java)
            user_data?.user_id = document.id
            user_data?.let {
                observableServiceProviderUserInfo.value = it
            }
        }.addOnFailureListener {

        }
    }

    fun sendRequest(appointment: AppointmentPOJO, selectedServices: ArrayList<SubServicesPOJO>,callback:SendRequestCallback) {
        repository.sendRequest(appointment).addOnSuccessListener { document->
            repository.addSubServicesToAppointment(selectedServices,document.id).addOnSuccessListener {
                callback.onRequestSentSuccessfully()
            }.addOnFailureListener {
                repository.removeRequestForFailure(document.id)
                callback.onRequestSendFailed(it)
            }
        }.addOnFailureListener {
            callback.onRequestSendFailed(it)
        }
    }


    interface SendRequestCallback{
        fun onRequestSentSuccessfully();
        fun onRequestSendFailed(exception: Exception)
    }

}