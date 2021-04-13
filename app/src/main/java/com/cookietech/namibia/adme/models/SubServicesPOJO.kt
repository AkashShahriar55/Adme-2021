package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class SubServicesPOJO():Parcelable {
    @Exclude
    var id:String? = null
    var service_name:String? = null
    var service_description:String? = null
    var service_charge:String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        service_name = parcel.readString()
        service_description = parcel.readString()
        service_charge = parcel.readString()
    }

    constructor(service_name: String?, service_description: String?, service_charge: String?) : this() {
        this.service_name = service_name
        this.service_description = service_description
        this.service_charge = service_charge
    }

    override fun toString(): String {
        return "SubServicesPOJO(id=$id, service_name=$service_name, service_description=$service_description, service_charge=$service_charge)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(service_name)
        parcel.writeString(service_description)
        parcel.writeString(service_charge)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubServicesPOJO> {
        override fun createFromParcel(parcel: Parcel): SubServicesPOJO {
            return SubServicesPOJO(parcel)
        }

        override fun newArray(size: Int): Array<SubServicesPOJO?> {
            return arrayOfNulls(size)
        }
    }


}