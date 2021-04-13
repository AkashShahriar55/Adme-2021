package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import java.util.*
import kotlin.collections.ArrayList

class ServicesPOJO():Parcelable {
     var category: String? = null
     var categoryId:String? = null
     var rating: Double
     var reviews: String? = null
     var startTime: String? = null
     var endTime: String? = null
     var user_name: String? = null
     var pic_url: String? = null
     var user_ref: String? = null
     var description: String? = null
     var latitude:String? = null
     var longitude:String? = null
     var feature_images: List<String> = ArrayList()
     var tags: String? = null
     var status: Boolean = true
     @Exclude
     var mServiceId: String? = null


    init {
        rating = 0.0
    }

    constructor(
        category: String?,
        categoryId:String?,
        description: String?,
        startTime: String?,
        endTime: String?,
        user_name: String?,
        pic_url: String?,
        user_ref: String?,
        latitude:String?,
        longitude:String?,
        feature_images: List<String>,
        services: MutableList<Map<String, String?>>,
        tags: String?,
        status: Boolean
    ) : this() {
        this.category = category
        this.categoryId = categoryId
        this.description = description
        rating = 0.0
        reviews = "0"
        this.startTime = startTime
        this.endTime = endTime
        this.user_name = user_name
        this.pic_url = pic_url
        this.user_ref = user_ref
        this.latitude = latitude
        this.longitude = longitude
        this.feature_images = feature_images
        this.tags = tags
        this.status = status
    }






    constructor(`in`: Parcel) : this() {
        category = `in`.readString()
        description = `in`.readString()
        rating = `in`.readDouble()
        reviews = `in`.readString()
        startTime = `in`.readString()
        endTime = `in`.readString()
        feature_images = `in`.createStringArrayList()!!
        tags = `in`.readString()
        user_name = `in`.readString()
        pic_url = `in`.readString()
        user_ref = `in`.readString()
        status =  `in`.readByte() != 0.toByte()
        mServiceId = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<ServicesPOJO> {
        override fun createFromParcel(parcel: Parcel): ServicesPOJO {
            return ServicesPOJO(parcel)
        }

        override fun newArray(size: Int): Array<ServicesPOJO?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(category)
        dest.writeString(description)
        dest.writeDouble(rating)
        dest.writeString(reviews)
        dest.writeString(startTime)
        dest.writeString(endTime)
        dest.writeStringList(feature_images)
        dest.writeString(tags)
        dest.writeString(user_name)
        dest.writeString(pic_url)
        dest.writeString(user_ref)
        dest.writeByte(if (status) 1 else 0)
        dest.writeString(mServiceId)
    }

    override fun toString(): String {
        return "ServicesPOJO(category=$category, categoryId=$categoryId, rating=$rating, reviews=$reviews, startTime=$startTime, endTime=$endTime, user_name=$user_name, pic_url=$pic_url, user_ref=$user_ref, description=$description, latitude=$latitude, longitude=$longitude, feature_images=$feature_images, tags=$tags, status=$status, mServiceId=$mServiceId, CREATOR=$CREATOR)"
    }


}