package com.cookietech.namibia.adme.ui.client.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_service_provider_details.*

class ServiceProviderDetailsActivity : AppCompatActivity() {

    var service: ServicesPOJO? = null
    var feature_image_adapter: ViewServiceImageSliderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider_details)

        service = intent.extras?.getParcelable("service")
        Log.d("service_debug", "onCreate: ${service?.user_name}")
        Log.d("service_debug", "onCreate: ${service?.category}")
        Log.d("service_debug", "onCreate: ${service?.feature_images?.size}")
        initializeViews()


    }

    private fun initializeViews() {

        setUpImageSlider()
        setUpServiceProviderName()
        setUpServiceCategory()

    }

    private fun setUpServiceCategory() {
        tv_catagory.text = service?.category
    }

    private fun setUpServiceProviderName() {
        tv_username.text = service?.user_name
    }

    private fun setUpImageSlider() {

        //feature image adapter setting

        imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        imageSlider.indicatorSelectedColor = Color.WHITE
        imageSlider.indicatorUnselectedColor = Color.GRAY
        feature_image_adapter = service?.let { ViewServiceImageSliderAdapter(this, it.feature_images) }
        feature_image_adapter?.let { imageSlider.setSliderAdapter(it) }
        //select_service_recyclerView.setAdapter(service_adapter)
        //review_recyclerView.adapter = reviewAdapter
    }
}