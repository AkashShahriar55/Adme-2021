package com.cookietech.namibia.adme.architecture.client.home

import android.util.Log
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class HomeRepository {

    fun fetchCategories(): CollectionReference {
        return FirebaseManager.mCategoryReference
    }


    fun fetchNearByServices(latitude:Double,longitude:Double,callback: NearbyServiceCallback){
        val data = hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
        Log.d("nearby_services", "updateFCMToken: data: $data")
        FirebaseManager.mFunctions.getHttpsCallable("getNearbyServices").call(data).addOnCompleteListener { task->
            if(task.isSuccessful){
                Log.d("nearby_services", "updateFCMToken result: ${task.result?.data.toString()}")
                //val jsonObject = JSONObject(jsonString)
                //Log.d("FCM_debug", "updateFCMToken: messgae: $jsonObject}")

                Log.d("nearby_services", "updateFCMToken: task successful: " + task.exception)

                // Log.d("search_debug", "getSearchResults: " + task.result.data)
                val allData: ArrayList<ServicesPOJO> = ArrayList<ServicesPOJO>()
                val jsonString = task.result.data.toString()
                Log.d("bishal_debug", "getSearchResults: $jsonString")

                try {
                    val jsonObject = JSONObject(jsonString)
                    val jsonArray = jsonObject.getJSONArray("data")
                    if (jsonArray.length() <= 0) {
                        /*searchResponse.setValue(
                            DatabaseResponse(
                                "search_response",
                                null,
                                DatabaseResponse.Response.Invalid_data
                            )
                        )*/
                        callback.onInvalidData()
                    } else {
                        for (i in 0 until jsonArray.length()) {
                            val `object` = jsonArray.getJSONObject(i)
                            val singleData: ServicesPOJO? = ServicesPOJO.fromJson(`object`)
                            if (singleData != null) allData.add(singleData)
                            Log.d("search_result", "onComplete: $singleData")
                        }
                        /* searchResponse.setValue(
                             DatabaseResponse(
                                 "search_response",
                                 null,
                                 DatabaseResponse.Response.Fetched
                             )
                         )*/
                        //searchResults.setValue(allData)
                        Log.d("search_debug", "getSearchResults: $allData")

                        callback.onFetchedNearbyService(allData)
                    }
                } catch (e: JSONException) {
                    /*searchResponse.setValue(
                        DatabaseResponse(
                            "search_response",
                            e,
                            DatabaseResponse.Response.Error
                        )
                    )*/
                    callback.onError()
                    e.printStackTrace()
                }


            }else{
                callback.onError()
                Log.d("nearby_services", "updateFCMToken: task not successful: " + task.exception)
            }
        }.addOnFailureListener {
            callback.onError()
            Log.d("nearby_services", "updateFCMToken: Failure: " + it.message)

        }
    }




}


interface NearbyServiceCallback{
    fun onInvalidData()
    fun onFetchedNearbyService(allData: ArrayList<ServicesPOJO> )
    fun onError()
}