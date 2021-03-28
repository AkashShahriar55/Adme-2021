package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class ServiceProviderPOJO() :Parcelable {
    var total_income:Double = 0.0
    var monthly_income:Double = 0.0
    var monthly_due:Double = 0.0
    var pressed:Int = 0
    var requested:Int = 0
    var completed:Int = 0
    var services = mutableListOf<String?>()


    constructor(parcel: Parcel) : this() {
        total_income = parcel.readDouble()
        monthly_income = parcel.readDouble()
        monthly_due = parcel.readDouble()
        pressed = parcel.readInt()
        requested = parcel.readInt()
        completed = parcel.readInt()
        val size = parcel.readInt()
        val services_temp = mutableListOf<String?>()
        for(i in 0..size){
            services_temp[i] = parcel.readString()
        }
        services = services_temp

    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeDouble(total_income)
        dest?.writeDouble(monthly_income)
        dest?.writeDouble(monthly_due)
        dest?.writeInt(pressed)
        dest?.writeInt(requested)
        dest?.writeInt(completed)
        val size = services.size
        dest?.writeInt(size)
        for(i in 0..size){
            dest?.writeString(services[i])
        }
    }

    companion object CREATOR : Parcelable.Creator<ServiceProviderPOJO> {
        override fun createFromParcel(parcel: Parcel): ServiceProviderPOJO {
            return ServiceProviderPOJO(parcel)
        }

        override fun newArray(size: Int): Array<ServiceProviderPOJO?> {
            return arrayOfNulls(size)
        }
    }
}