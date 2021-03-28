package com.cookietech.namibia.adme.architecture.serviceProvider

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.today.ServiceProviderRepository
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import java.lang.Exception

class ServiceProviderViewModel: ViewModel() {
    val TAG = "service_debug"
    val service_provider_data = SingleLiveEvent<ServiceProviderPOJO?>()
    val repository = ServiceProviderRepository()

    init {
        Log.d(TAG, " view model initiated : ")
    }


    fun createOrFetchServiceData(callback: ServiceProviderDataCallback){
        Log.d(TAG, " started  : ")
        FirebaseManager.mFirebaseUser?.apply {
            repository.createOrFetchServiceProvider(uid,callback,service_provider_data)
        }
    }


}