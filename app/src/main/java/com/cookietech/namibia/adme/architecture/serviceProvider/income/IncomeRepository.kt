package com.cookietech.namibia.adme.architecture.serviceProvider.income

import android.util.Log
import com.cookietech.namibia.adme.helper.IncomeHelper
import com.cookietech.namibia.adme.helper.IncomeHelper.monthNames
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.UserPOJO
import com.github.mikephil.charting.data.BarEntry
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
    val history = TreeMap<Int, BarEntry>()

    init {
        for (i in 0..11){
            history[i] = BarEntry(i.toFloat(),0f)
        }
    }



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



                    for (doc in result) {
                        Log.d("income_history_debug", "${doc.id} => ${doc.data}")
                        doc.getLong("monthly_income")?.let {
                            //cities.add(it)
                            history[IncomeHelper.getMonthIndex(doc.id)] = BarEntry(IncomeHelper.getMonthIndex(doc.id).toFloat(),it.toFloat())
                        }
                    }

                    callback.onIncomeHistoryFetchSuccess(history)

                }
                .addOnFailureListener { exception ->
                    callback.onIncomeHistoryFetchSuccess(history)
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
        fun onIncomeHistoryFetchSuccess(history: TreeMap<Int, BarEntry>)
        fun onIncomeHistoryFetchError()
    }
}