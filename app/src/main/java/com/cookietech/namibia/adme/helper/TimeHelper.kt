package com.cookietech.namibia.adme.helper

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object TimeHelper {

    fun getTimeDifference(timestamp: Timestamp) : String{

        val startDate : Date = timestamp.toDate()
        val now : Date = Timestamp.now().toDate()
        val difference: Long = now.time - startDate.time
        val second = difference/1000
        val min = second/60
        val hour = min/60
        val day  = hour/24
        //val week =  day/7

        if (day.toInt() > 7){
            //actual time
            val simpleDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return simpleDate.format(startDate)
        }
        else if (day.toInt() in 1..7){
            //days ago

            return  ""+day.toInt() + " days Ago"
        }
        else if (day.toInt() < 1 && hour.toInt() in 1..24){
            //hour Ago
            return  ""+hour.toInt() + " hours Ago"
        }
        else if(hour.toInt() < 1 && min.toInt() in 1..60){
            // min ago
            return  ""+min.toInt() + " minutes Ago"
        }
        else if(min.toInt() < 1){
            //just now
            return "just now"
        }


        return ""
    }
}