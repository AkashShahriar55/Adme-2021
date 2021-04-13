package com.cookietech.namibia.adme.architecture.client.home

import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.today.AddServiceRepository
import com.cookietech.namibia.adme.models.ServiceCategory
import com.cookietech.namibia.adme.utils.SingleLiveEvent

class ClientHomeViewModel: ViewModel() {

    val categories: SingleLiveEvent<ArrayList<ServiceCategory>> = SingleLiveEvent()
    val homeRepository = HomeRepository()

    init {
        fetchServiceCategoryData()
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

}