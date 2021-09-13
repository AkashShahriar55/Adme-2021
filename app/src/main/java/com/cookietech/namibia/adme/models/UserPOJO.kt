package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

class UserPOJO(@Exclude var user_id: String,
               var user_name:String,
               var email: String? = "null",
               var phone: String,
               var privacy_phone_is_public: Boolean = true,
               var privacy_email_is_public: Boolean = true,
               var registration_date: Timestamp? = null,
               var status_is_online: Boolean = true,
               var lattitude: String? = "null",
               var longitude: String? = "null",
               var profile_image_url: String? = "null",
               var isServiceProvider: Boolean = false,
               var isActive: Boolean = true,
               var user_permission: String = "user",
               var user_info_updated:Boolean = false,
               var hasUnreadNotifSP: Boolean = false,
               var hasUnreadNotifClient: Boolean = false) : Parcelable {

    constructor() : this(
        "null",
        "null",
        "null",
        "null",
        true,
        true,
        null,
        true,
        "null",
        "null",
        "null",
        false,
        true,
        "user",
        false,
        false,
        false)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "null",
        parcel.readString() ?: "null",
        parcel.readString(),
        parcel.readString() ?: "null",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "user",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(user_name)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeByte(if (privacy_phone_is_public) 1 else 0)
        parcel.writeByte(if (privacy_email_is_public) 1 else 0)
        parcel.writeParcelable(registration_date, flags)
        parcel.writeByte(if (status_is_online) 1 else 0)
        parcel.writeString(lattitude)
        parcel.writeString(longitude)
        parcel.writeString(profile_image_url)
        parcel.writeByte(if (isServiceProvider) 1 else 0)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeString(user_permission)
        parcel.writeByte(if(user_info_updated) 1 else 0)
        parcel.writeByte(if(hasUnreadNotifSP) 1 else 0)
        parcel.writeByte(if(hasUnreadNotifClient) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserPOJO> {
        override fun createFromParcel(parcel: Parcel): UserPOJO {
            return UserPOJO(parcel)
        }

        override fun newArray(size: Int): Array<UserPOJO?> {
            val array = mutableListOf<UserPOJO?>()
            for (i in 0..size){
                array.add(UserPOJO())
            }
            return array.toTypedArray()
        }
    }
}