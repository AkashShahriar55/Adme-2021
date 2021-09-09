package com.cookietech.namibia.adme.architecture.client.home

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
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

    val categories: SingleLiveEvent<ArrayList<ServiceCategory>> = SingleLiveEvent()
    val homeRepository = HomeRepository()
    val services = SingleLiveEvent<ArrayList<ServicesPOJO>>()
    var servicesListenerRegistration:ListenerRegistration? = null

    init {
        fetchServiceCategoryData()
        fetchServiceProviderData()
    }

    private fun fetchServiceCategoryData() {

        homeRepository.fetchCategories().addOnSuccessListener { documents->
            val cats = ArrayList<ServiceCategory>()
            for (document in documents) {
                val cat = document.toObject(ServiceCategory::class.java)
                cat.id = document.id
                cats.add(cat)
            }
            categories.value = cats
        }.addOnFailureListener {

        }

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
    }

}