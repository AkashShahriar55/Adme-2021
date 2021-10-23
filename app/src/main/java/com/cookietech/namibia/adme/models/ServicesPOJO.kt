package com.cookietech.namibia.adme.models

import android.os.Parcel
import android.os.Parcelable
import com.cookietech.namibia.adme.ui.client.home.search.SearchData
import com.google.firebase.firestore.Exclude
import org.json.JSONException
import org.json.JSONObject
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
     var feature_images: MutableList<String> = ArrayList()
     var tags: String? = null
     var status: Boolean = true
     @Exclude
     var mServiceId: String? = null

    constructor(parcel: Parcel) : this() {
        category = parcel.readString()
        categoryId = parcel.readString()
        rating = parcel.readDouble()
        reviews = parcel.readString()
        startTime = parcel.readString()
        endTime = parcel.readString()
        user_name = parcel.readString()
        pic_url = parcel.readString()
        user_ref = parcel.readString()
        description = parcel.readString()
        latitude = parcel.readString()
        longitude = parcel.readString()
        feature_images = parcel.createStringArrayList()!!
        tags = parcel.readString()
        status = parcel.readByte() != 0.toByte()
        mServiceId = parcel.readString()
    }


    init {
        rating = 0.0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(categoryId)
        parcel.writeDouble(rating)
        parcel.writeString(reviews)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(user_name)
        parcel.writeString(pic_url)
        parcel.writeString(user_ref)
        parcel.writeString(description)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeStringList(feature_images)
        parcel.writeString(tags)
        parcel.writeByte(if (status) 1 else 0)
        parcel.writeString(mServiceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServicesPOJO> {
        override fun createFromParcel(parcel: Parcel): ServicesPOJO {
            return ServicesPOJO(parcel)
        }

        override fun newArray(size: Int): Array<ServicesPOJO?> {
            return arrayOfNulls(size)
        }

        fun fromJson(jsonObject: JSONObject): ServicesPOJO? {
            //val searchData: SearchData =  SearchData()
            val servicesPOJO : ServicesPOJO = ServicesPOJO()
            return try {
                servicesPOJO.mServiceId = jsonObject.getString("id")
                servicesPOJO.category = jsonObject.getString("category")
                servicesPOJO.categoryId = jsonObject.getString("categoryId")
                servicesPOJO.description = jsonObject.getString("description")

                servicesPOJO.latitude = jsonObject.getString("latitude")
                servicesPOJO.longitude = jsonObject.getString("longitude")
                /*servicesPOJO.max_charge = jsonObject.getString("max_charge")
                servicesPOJO.min_charge = jsonObject.getString("min_charge")*/

                servicesPOJO.pic_url = jsonObject.getString("pic_url")
                servicesPOJO.rating = jsonObject.getString("rating").toDouble()
                servicesPOJO.reviews = jsonObject.getString("reviews")
                servicesPOJO.user_name = jsonObject.getString("user_name")
                servicesPOJO.user_ref = jsonObject.getString("user_ref")

                servicesPOJO
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }
    }




}