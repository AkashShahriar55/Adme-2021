package com.cookietech.namibia.adme.architecture.serviceProvider.income

import android.util.Log
import com.cookietech.namibia.adme.helper.IncomeHelper
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.UserPOJO
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class IncomeRepository {

    private val listeners:ArrayList<ListenerRegistration>  = ArrayList()

    fun getCurrentMonthIncome(callback: CurrentMonthIncomeCallback){

        FirebaseManager.currentUser?.apply {
           val listener = FirebaseManager.mUserRef
                .document(user_id)
                .collection("data")
                .document("service_provider")
                .collection("income_history")
                .document("monthly_income")
               .collection(IncomeHelper.getCurrentYear())
               .document(IncomeHelper.getCurrentMonth())
                .addSnapshotListener{ snapshot, error ->
                    if (error != null) {
                        Log.d("monthly_income_debug", "Listen failed.", error)
                        callback.onCurrentMonthIncomeError()
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("monthly_income_debug", "Current data: ${snapshot.data}")

                        callback.onCurrentMonthIncomeFetchSuccess(
                            snapshot.data?.get("monthly_due") as Long,
                            snapshot.data?.get("monthly_income") as Long
                        )
                    } else {
                        callback.onCurrentMonthIncomeError()
                        Log.d("monthly_income_debug", "Current data: null")
                    }
                }
            listeners.add(listener)
        }

    }

    fun removeListeners(){
        for(listener in listeners){
            listener.remove()
        }
    }

    interface CurrentMonthIncomeCallback{
        fun onCurrentMonthIncomeFetchSuccess(monthlyDue: Long, monthlyIncome: Long)
        fun onCurrentMonthIncomeError()
    }
}