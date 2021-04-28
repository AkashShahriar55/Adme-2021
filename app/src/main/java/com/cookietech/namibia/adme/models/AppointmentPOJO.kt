package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class AppointmentPOJO(var client_name:String,
                           var client_phone:String,
                           var client_ref:String,
                           var client_quotation:String,
                           var client_price:String
                           , var client_latitude:String
                           , var client_longitude:String
                           , var client_address:String
                           , var client_time:String
                           , var service_provider_name:String
                           , var service_provider_phone:String
                           , var service_provider_ref:String
                           , var service_name:String
                           , var service_ref:String
                           , var service_provider_quotation:String?
                           , var service_provider_price:String?
                           , var service_provider_latitude:String
                           , var service_provider_longitude:String
                           , var service_provider_time:String?
                           , var approved:Boolean
                           , var state:String
                           , var client_profile_pic:String?
                           , var service_provider_pic:String?
                           , var time_in_millis:String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString().toString()
    ) {
        id = parcel.readString().toString()
    }


    @Exclude var id:String? = null


    constructor() : this("","","","","","","","","","","","","","","","","","","",false,"","","","")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(client_name)
        parcel.writeString(client_phone)
        parcel.writeString(client_ref)
        parcel.writeString(client_quotation)
        parcel.writeString(client_price)
        parcel.writeString(client_latitude)
        parcel.writeString(client_longitude)
        parcel.writeString(client_address)
        parcel.writeString(client_time)
        parcel.writeString(service_provider_name)
        parcel.writeString(service_provider_phone)
        parcel.writeString(service_provider_ref)
        parcel.writeString(service_name)
        parcel.writeString(service_ref)
        parcel.writeString(service_provider_quotation)
        parcel.writeString(service_provider_price)
        parcel.writeString(service_provider_latitude)
        parcel.writeString(service_provider_longitude)
        parcel.writeString(service_provider_time)
        parcel.writeByte(if (approved) 1 else 0)
        parcel.writeString(state)
        parcel.writeString(client_profile_pic)
        parcel.writeString(service_provider_pic)
        parcel.writeString(time_in_millis)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppointmentPOJO> {
        override fun createFromParcel(parcel: Parcel): AppointmentPOJO {
            return AppointmentPOJO(parcel)
        }

        override fun newArray(size: Int): Array<AppointmentPOJO?> {
            return arrayOfNulls(size)
        }
    }
}
