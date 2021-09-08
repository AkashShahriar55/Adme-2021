package com.cookietech.namibia.adme.architecture.client.home

import android.util.Log
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.ui.client.home.search.SearchData
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SearchServiceRepository {

    fun getSearchResults(searchQuery: String, searchCallback: SearchCallback){
        FirebaseManager.mFunctions.getHttpsCallable("getServiceSearchResults").call(searchQuery).addOnCompleteListener { task->
            if(task.isSuccessful){
               // Log.d("search_debug", "getSearchResults: " + task.result.data)
                val allData: ArrayList<SearchData> = ArrayList<SearchData>()
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

                        searchCallback.onInvalidData()
                    } else {
                        for (i in 0 until jsonArray.length()) {
                            val `object` = jsonArray.getJSONObject(i)
                            val singleData: SearchData? = SearchData.fromJson(`object`)
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

                        searchCallback.onFetchedSearchResult(allData)
                    }
                } catch (e: JSONException) {
                    /*searchResponse.setValue(
                        DatabaseResponse(
                            "search_response",
                            e,
                            DatabaseResponse.Response.Error
                        )
                    )*/
                        searchCallback.onError()
                    e.printStackTrace()
                }


            }else{
                Log.d("search_debug", "getSearchResults: " + task.exception)
            }
        }.addOnFailureListener {

        }
    }

    interface SearchCallback {
        fun onInvalidData()
        fun onError()
        fun onFetchedSearchResult(allData: ArrayList<SearchData>)
        fun onFetchStarted()
    }
}