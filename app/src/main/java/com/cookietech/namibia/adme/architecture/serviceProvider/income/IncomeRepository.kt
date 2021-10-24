package com.cookietech.namibia.adme.architecture.serviceProvider.income

import android.util.Log
import com.cookietech.namibia.adme.helper.IncomeHelper
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.UserPOJO
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.hadiidbouk.charts.BarData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IncomeRepository {

    private val listeners:ArrayList<ListenerRegistration>  = ArrayList()



    fun getIncomeHistory(callback: IncomeHistoryCallback){
        FirebaseManager.currentUser?.apply {
            FirebaseManager.mUserRef
                .document(user_id)
                .collection("data")
                .document("service_provider")
                .collection("income_history")
                .document("monthly_income")
                .collection(IncomeHelper.getCurrentYear())
                .get()
                .addOnSuccessListener { result ->
                    val history = TreeMap<Int, BarData>()
                    for (doc in result) {
                        Log.d("income_history_debug", "${doc.id} => ${doc.data}")
                        doc.getLong("monthly_income")?.let {
                            //cities.add(it)
                            history.put(IncomeHelper.getMonthIndex(doc.id),
                                BarData(doc.id,it.toFloat(),it.toString())
                            )
                        }
                    }

                    callback.onIncomeHistoryFetchSuccess(history)

                }
                .addOnFailureListener { exception ->
                    Log.d("income_history_debug", "Error getting documents: ", exception)

                }
        }

    }

    fun removeListeners(){
        for(listener in listeners){
            listener.remove()
        }
    }


    interface IncomeHistoryCallback{
        fun onIncomeHistoryFetchSuccess(history: TreeMap<Int, BarData>)
        fun onIncomeHistoryFetchError()
    }
}