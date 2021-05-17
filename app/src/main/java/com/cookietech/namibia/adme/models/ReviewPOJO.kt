package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable

data class ReviewPOJO(var client_name:String
                        , var client_ref:String
                        , var provider_name:String
                        , var provider_ref:String
                        , var rating:Float = 0.0f
                        , var review:String?
                        , var invoice_link:String
                        , var income:String
                        , var review_time:String
                        , var appointment_ref:String
                        , var service_ref:String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()

    ) {
    }

    constructor() : this("","","","",0.0f,"","","","","","")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(client_name)
        parcel.writeString(client_ref)
        parcel.writeString(provider_name)
        parcel.writeString(provider_ref)
        parcel.writeFloat(rating)
        parcel.writeString(review)
        parcel.writeString(invoice_link)
        parcel.writeString(income)
        parcel.writeString(review_time)
        parcel.writeString(appointment_ref)
        parcel.writeString(service_ref)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReviewPOJO> {
        override fun createFromParcel(parcel: Parcel): ReviewPOJO {
            return ReviewPOJO(parcel)
        }

        override fun newArray(size: Int): Array<ReviewPOJO?> {
            return arrayOfNulls(size)
        }
    }
}