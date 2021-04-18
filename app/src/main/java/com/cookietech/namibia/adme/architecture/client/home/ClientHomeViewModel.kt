package com.cookietech.namibia.adme.architecture.client.home

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceCategory
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.ui.client.home.search.SearchData
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.firebase.firestore.ListenerRegistration

class ClientHomeViewModel: ViewModel() {

    val categories: SingleLiveEvent<ArrayList<ServiceCategory>> = SingleLiveEvent()
    val homeRepository = HomeRepository()
    val services = SingleLiveEvent<ArrayList<SearchData>>()
    var servicesListenerRegistration:ListenerRegistration? = null

    init {
        fetchServiceCategoryData()
        fetchServiceProviderData()
    }

    private fun fetchServiceCategoryData() {

        homeRepository.fetchCategories().addOnSuccessListener { documents->
            val cats = ArrayList<ServiceCategory>()
            for (document in documents) {
                val cat = document.toObject(ServiceCategory::class.java)
                cat.id = document.id
                cats.add(cat)
            }
            categories.value = cats
        }.addOnFailureListener {

        }

    }


    private fun fetchServiceProviderData(){
        FirebaseManager.mServiceListReference.addSnapshotListener { value, error ->
            error?.apply {
                Log.d("service_debug", "fetchServiceProviderData: $message")
                return@apply
            }

            value?.let { documents->

                val list = arrayListOf<SearchData>()
                for (document in documents){
                    val service = document.toObject(SearchData::class.java)
                    service.id = document.id
                    service.let { list.add(it) }
                }
                services.value = list
                Log.d("service_debug", "fetchServiceProviderData: ${list.size}")
            }
        }
    }

    fun generateMarkerBitmap(context: Context,profile_photo: Bitmap): Bitmap? {
        var profile_photo = profile_photo
        val background = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val mainCanvas = Canvas(background)
        var markerImage = BitmapFactory.decodeResource(context.resources, R.drawable.marker_with_photo)
        markerImage = Bitmap.createScaledBitmap(markerImage, 100, 100, false)
        mainCanvas.drawBitmap(markerImage, 0f, 0f, null)
        val roundedImage = Bitmap.createBitmap(
            66,
            66, Bitmap.Config.ARGB_8888
        )
        val profileImageCanvas = Canvas(roundedImage)
        //Bitmap mainProfileImage = BitmapFactory.decodeResource(getResources(),R.drawable.test_image);
        profile_photo = Bitmap.createScaledBitmap(profile_photo, 65, 65, false)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, 66, 66)
        paint.isAntiAlias = true
        profileImageCanvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        profileImageCanvas.drawCircle(33f, 33f, 33f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        profileImageCanvas.drawBitmap(profile_photo, rect, rect, paint)
        mainCanvas.drawBitmap(roundedImage, 18f, 4f, null)
        val bitmapDrawable = BitmapDrawable(background)
        return bitmapDrawable.bitmap
    }


    override fun onCleared() {
        super.onCleared()
        servicesListenerRegistration?.remove()
        servicesListenerRegistration = null
    }

}