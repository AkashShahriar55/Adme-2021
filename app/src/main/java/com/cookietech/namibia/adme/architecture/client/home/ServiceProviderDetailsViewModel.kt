package com.cookietech.namibia.adme.architecture.client.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.helper.IncomeHelper
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.*
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.github.mikephil.charting.data.BarEntry
import java.lang.Exception

class ServiceProviderDetailsViewModel: ViewModel() {
    val repository = ServiceProviderDetailsRepository()
    val observableSubServices = SingleLiveEvent<ArrayList<SubServicesPOJO>>()
    val observableServiceFullDetails = SingleLiveEvent<ServicesPOJO>()
    val observableServiceProviderUserInfo = SingleLiveEvent<UserPOJO>()
    val observableReviewData = SingleLiveEvent<ArrayList<ReviewPOJO>>()

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

    fun fetchReviewData(providerRef:String,serviceRef:String){
        repository.fetchReviewData(providerRef,serviceRef)
            .addOnSuccessListener { result ->
                val reviewList = ArrayList<ReviewPOJO>()
                for (doc in result) {
                    Log.d("income_history_debug", "${doc.id} => ${doc.data}")
                    /*doc.getLong("monthly_income")?.let {
                        //cities.add(it)
                        history[IncomeHelper.getMonthIndex(doc.id)] = BarEntry(IncomeHelper.getMonthIndex(doc.id).toFloat(),it.toFloat())
                    }*/
                    val review = doc.toObject(ReviewPOJO::class.java)
                    reviewList.add(review)
                }

                /*callback.onIncomeHistoryFetchSuccess(history)*/
                if (reviewList.isNotEmpty()){
                    observableReviewData.value = reviewList
                }

            }
            .addOnFailureListener { exception ->
                /*callback.onIncomeHistoryFetchSuccess(history)*/
                Log.d("income_history_debug", "Error getting documents: ", exception)

            }
    }


    interface SendRequestCallback{
        fun onRequestSentSuccessfully();
        fun onRequestSendFailed(exception: Exception)
    }

}