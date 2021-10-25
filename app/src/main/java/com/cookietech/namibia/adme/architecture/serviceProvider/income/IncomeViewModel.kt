package com.cookietech.namibia.adme.architecture.serviceProvider.income


import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.github.mikephil.charting.data.BarEntry
import com.hadiidbouk.charts.BarData
import java.util.*
import kotlin.collections.ArrayList


class IncomeViewModel : ViewModel() {

    private val repository: IncomeRepository = IncomeRepository()

    val incomeHistoryListener = SingleLiveEvent<ArrayList<BarEntry>>()

    init {

        getIncomeHistory()
    }

    private fun getIncomeHistory() {
        repository.getIncomeHistory(object : IncomeRepository.IncomeHistoryCallback{
            override fun onIncomeHistoryFetchSuccess(history: TreeMap<Int, BarEntry>) {
                val barDataList = ArrayList(history.values)
                //incomeHistoryListener.value = ArrayList(barDataList.subList(0,6))
                incomeHistoryListener.value = barDataList
            }

            override fun onIncomeHistoryFetchError() {

            }


        })
    }



    override fun onCleared() {
        super.onCleared()
        repository.removeListeners()
    }
}