package com.cookietech.namibia.adme.architecture.serviceProvider

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.today.ServiceProviderRepository
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.utils.GoogleMapUtils
import java.lang.Exception

class ServiceProviderViewModel: ViewModel() {
    val TAG = "service_debug"
    val service_provider_data = MutableLiveData<ServiceProviderPOJO?>()
    val repository = ServiceProviderRepository()
    val services = MutableLiveData<ArrayList<ServicesPOJO>>()
    val observableAppointments = MutableLiveData<ArrayList<AppointmentPOJO>>()

    init {
        Log.d(TAG, " view model initiated : ")
        services.value = ArrayList()
        fetchAllServices()
        fetchAllAppointments()
    }

    private fun fetchAllAppointments() {
        FirebaseManager.mFirebaseUser?.apply {
            repository.fetchAllAppointments(uid,observableAppointments)
        }
    }


    fun createOrFetchServiceData(callback: ServiceProviderDataCallback){
        Log.d(TAG, " started  : ")
        FirebaseManager.mFirebaseUser?.apply {
            repository.createOrFetchServiceProvider(uid,callback,service_provider_data)
        }
    }

    private fun fetchAllServices() {
        repository.fetchAllServices(object : ServiceProviderRepository.AllServiceFetch {
            override fun onFetchSuccess(services: ArrayList<ServicesPOJO>) {
                Log.d("database_debug", "onFetchSuccess: " + services.size)
                this@ServiceProviderViewModel.services.value = services
            }

            override fun onFetchFailed(exception: Exception) {
                this@ServiceProviderViewModel.services.value = ArrayList()
            }

        })
    }

    fun generateMarkerBitmap(requireContext: Context, decodeResource: Bitmap): Bitmap? {
        return GoogleMapUtils.generateMarkerBitmap(requireContext,decodeResource)
    }


    override fun onCleared() {
        super.onCleared()
        repository.removeListeners()
    }


}