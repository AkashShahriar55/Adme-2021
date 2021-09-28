package com.cookietech.namibia.adme.architecture.client.home

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceCategory
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.ui.client.home.search.SearchData
import com.cookietech.namibia.adme.utils.GoogleMapUtils
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.firebase.firestore.ListenerRegistration

class ClientHomeViewModel: ViewModel() {

    val categories: MutableLiveData<ArrayList<ServiceCategory>> = MutableLiveData()
    val homeRepository = HomeRepository()
    val services = MutableLiveData<ArrayList<ServicesPOJO>>()
    var servicesListenerRegistration:ListenerRegistration? = null
    var categoryListenerRegistration:ListenerRegistration? = null

    init {
        fetchServiceCategoryData()
    }

    private fun fetchServiceCategoryData() {

        categoryListenerRegistration = homeRepository.fetchCategories().addSnapshotListener { documents,error->
            error?.let {
                return@addSnapshotListener
            }

            documents?.let {
                val cats = ArrayList<ServiceCategory>()
                for (document in documents) {
                    val cat = document.toObject(ServiceCategory::class.java)
                    cat.id = document.id
                    cats.add(cat)
                }
                categories.value = cats
            }

        }

    }


    public fun fetchNearbyServices(latitude:Double,longitude:Double){
        homeRepository.fetchNearByServices(latitude, longitude,object :NearbyServiceCallback{
            override fun onInvalidData() {
                Log.d("map_service", "onInvalidData: ")
            }

            override fun onFetchedNearbyService(allData: java.util.ArrayList<ServicesPOJO>) {
                Log.d("map_service", "onFetchedNearbyService: $allData")
                if(allData.size > 0)
                    services.value = allData
            }

            override fun onError() {
                Log.d("map_service", "onError: ")
            }

        })
    }


    private fun fetchServiceProviderData(){




        FirebaseManager.mServiceListReference.addSnapshotListener { value, error ->
            error?.apply {
                Log.d("service_debug", "fetchServiceProviderData: $message")
                return@apply
            }

            value?.let { documents->

                val list = arrayListOf<ServicesPOJO>()
                for (document in documents){
                    val service = document.toObject(ServicesPOJO::class.java)
                    service.mServiceId = document.id
                    service.let { list.add(it) }
                }
                services.value = list
                Log.d("service_debug", "fetchServiceProviderData: ${list.size}")
            }
        }
    }

    fun generateMarkerBitmap(context: Context,profile_photo: Bitmap): Bitmap? {
        return GoogleMapUtils.generateMarkerBitmap(context, profile_photo)
    }


    override fun onCleared() {
        super.onCleared()
        servicesListenerRegistration?.remove()
        servicesListenerRegistration = null
        categoryListenerRegistration?.remove()
        categoryListenerRegistration = null
    }

}