package com.cookietech.namibia.adme.architecture.client.home

import android.util.Log
import com.cookietech.namibia.adme.managers.FirebaseManager

class SearchServiceRepository {

    fun getSearchResults(searchQuery:String){
        FirebaseManager.mFunctions.getHttpsCallable("getServiceSearchResults").call(searchQuery).addOnCompleteListener {task->
            if(task.isSuccessful){
                Log.d("search_debug", "getSearchResults: " + task.result.data)
            }else{
                Log.d("search_debug", "getSearchResults: " + task.exception)
            }
        }.addOnFailureListener {

        }
    }
}