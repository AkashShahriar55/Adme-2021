package com.cookietech.namibia.adme.ui.client.home.search

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

class SearchData(
    var id: String? = null,
    var category: String? = null,
    var categoryId: String? = null,
    var description: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var max_charge: String? = null,
    var min_charge: String? = null,
    var pic_url: String? = null,
    var rating: String? = null,
    var reviews: String? = null,
    var user_name: String? = null,
    var user_ref: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(category)
        parcel.writeString(categoryId)
        parcel.writeString(description)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(max_charge)
        parcel.writeString(min_charge)
        parcel.writeString(pic_url)
        parcel.writeString(rating)
        parcel.writeString(reviews)
        parcel.writeString(user_name)
        parcel.writeString(user_ref)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "SearchData(id=$id, category=$category, categoryId=$categoryId, description=$description, latitude=$latitude, longitude=$longitude, max_charge=$max_charge, min_charge=$min_charge, pic_url=$pic_url, rating=$rating, reviews=$reviews, user_name=$user_name, user_ref=$user_ref)"
    }

    companion object CREATOR : Parcelable.Creator<SearchData> {
        override fun createFromParcel(parcel: Parcel): SearchData {
            return SearchData(parcel)
        }

        override fun newArray(size: Int): Array<SearchData?> {
            return arrayOfNulls(size)
        }



        fun fromJson(jsonObject: JSONObject): SearchData? {
            val searchData: SearchData =  SearchData()
            return try {
                searchData.id = jsonObject.getString("id")
                searchData.category = jsonObject.getString("category")
                searchData.categoryId = jsonObject.getString("categoryId")
                searchData.description = jsonObject.getString("description")

                searchData.latitude = jsonObject.getString("latitude")
                searchData.longitude = jsonObject.getString("longitude")
                searchData.max_charge = jsonObject.getString("max_charge")
                searchData.min_charge = jsonObject.getString("min_charge")

                searchData.pic_url = jsonObject.getString("pic_url")
                searchData.rating = jsonObject.getString("rating")
                searchData.reviews = jsonObject.getString("reviews")
                searchData.user_name = jsonObject.getString("user_name")
                searchData.user_ref = jsonObject.getString("user_ref")

                searchData
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }
    }






}