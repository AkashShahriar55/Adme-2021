package com.cookietech.namibia.adme.architecture.serviceProvider.today

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceCategory
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.models.UserPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import java.util.*
import kotlin.collections.ArrayList

class AddServiceViewModel : ViewModel() {
    var endTime = Calendar.getInstance()
    var startTime = Calendar.getInstance()
    var selectedCategory: ServiceCategory? = null
    val categories: SingleLiveEvent<ArrayList<ServiceCategory>> = SingleLiveEvent()
    val completedFlags = mutableListOf(false,false,false)
    val addServiceRepository = AddServiceRepository()
    val service = ServicesPOJO()
    val isOverviewSaved = false
    val imageUris: ArrayList<Uri?> = arrayListOf(null, null, null)
    val subServicesLiveData = SingleLiveEvent<ArrayList<SubServicesPOJO>?>()

    init {
        Log.d("service_debug", ": add service view model initiated")
        fetchServiceCategoryData();
        startTime.set(Calendar.HOUR_OF_DAY, 10)
        startTime.set(Calendar.MINUTE, 0)
        endTime.set(Calendar.HOUR_OF_DAY, 16)
        endTime.set(Calendar.MINUTE, 0)
    }

    private fun fetchServiceCategoryData() {

        addServiceRepository.fetchCategories().addOnSuccessListener { documents->
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

    public fun addSubService(subService:SubServicesPOJO){
        if(subServicesLiveData.value == null){
            val servicesArray = ArrayList<SubServicesPOJO>()
            servicesArray.add(subService)
            subServicesLiveData.value = servicesArray
        }else{
            val serviceArray = subServicesLiveData.value
            serviceArray?.add(subService)
            subServicesLiveData.value = serviceArray
        }
    }

     fun uploadImagesToServer(uri:Uri,callback: AddServiceRepository.UploadImageCallback) {
         addServiceRepository.uploadImagesToServer(uri, callback)
     }

    fun updateDatabase(): Task<DocumentReference>? {
        var tags = ""
        subServicesLiveData.value?.let {
            for (service in it){
                Log.d("database_debug", "updateDatabase: $tags")
                tags += ", ${service.service_name}"
            }
        }
        service.tags = tags
        return addServiceRepository.updateDatabase(service)
    }

    fun updateSubServices(id: String): Task<Void>? {
        if(subServicesLiveData.value != null){
            return addServiceRepository.updateSubServices(subServicesLiveData.value!!,id)
        }
        return null

    }
}