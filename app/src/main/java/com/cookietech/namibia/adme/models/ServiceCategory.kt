package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class ServiceCategory() : Parcelable {
    @Exclude
    var id:String? = null
    var category:String? = null
    var icon:String? = null
    var sub_categories:List<String>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        category = parcel.readString()
        icon = parcel.readString()
        sub_categories = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(category)
        parcel.writeString(icon)
        parcel.writeStringList(sub_categories)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceCategory> {
        override fun createFromParcel(parcel: Parcel): ServiceCategory {
            return ServiceCategory(parcel)
        }

        override fun newArray(size: Int): Array<ServiceCategory?> {
            return arrayOfNulls(size)
        }
    }
}