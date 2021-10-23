package com.cookietech.namibia.adme.helper

import android.util.Log
import java.lang.reflect.Array
import java.util.*

object IncomeHelper {

    val monthNames = arrayListOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")

    fun getTotalIncome(totalIncome: Double) : String{
        //Log.d("income_debug", "getTotalIncome: ${totalIncome/100000}")
        return if (totalIncome/100000 >= 1){
            val income = totalIncome/1000
            "$income k"
        } else{
            totalIncome.toString()
        }
    }

    fun getCurrentYearMonth(): String{
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        /*Log.d("income_debug", "year: $year")
        Log.d("income_debug", "month: ${monthNames[month]}")*/
        return "$year-$month"
    }
    fun getCurrentYear(): String{
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        return year.toString()
    }
    fun getCurrentMonth(): String{
        val c: Calendar = Calendar.getInstance()
        //val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        return monthNames[month]
    }
}