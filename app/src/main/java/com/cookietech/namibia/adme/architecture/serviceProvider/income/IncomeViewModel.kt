package com.cookietech.namibia.adme.architecture.serviceProvider.income

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.models.NotificationPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.firebase.firestore.ListenerRegistration

class IncomeViewModel : ViewModel() {

    private val repository: IncomeRepository = IncomeRepository()
    val monthlyDueListener =  SingleLiveEvent<Long>()
    val monthlyIncomeListener =  SingleLiveEvent<Long>()

    init {
        monthlyDueListener.value = 0.toLong()
        monthlyIncomeListener.value = 0.toLong()
        getCurrentMonthIncome()
    }

    private fun getCurrentMonthIncome(){
        repository.getCurrentMonthIncome(object : IncomeRepository.CurrentMonthIncomeCallback{
            override fun onCurrentMonthIncomeFetchSuccess(
                monthlyDue: Long,
                monthlyIncome: Long
            ) {

                monthlyDueListener.value = monthlyDue
                monthlyIncomeListener.value = monthlyIncome

            }

            override fun onCurrentMonthIncomeError() {
                monthlyDueListener.value = 0.toLong()
                monthlyIncomeListener.value = 0.toLong()

            }

        })
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListeners()
    }
}