package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

class NotificationPOJO(var text: String? = null,
                       var  time: Timestamp? = null,
                       var mode: String? = null,
                       var type: String? = null,
                       var reference: String? = null,
                       var isSeen: Boolean? = false,
                       @Exclude
                       var id: String? = null) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeParcelable(time, flags)
        parcel.writeString(mode)
        parcel.writeString(type)
        parcel.writeString(reference)
        parcel.writeValue(isSeen)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationPOJO> {
        override fun createFromParcel(parcel: Parcel): NotificationPOJO {
            return NotificationPOJO(parcel)
        }

        override fun newArray(size: Int): Array<NotificationPOJO?> {
            return arrayOfNulls(size)
        }
    }


}