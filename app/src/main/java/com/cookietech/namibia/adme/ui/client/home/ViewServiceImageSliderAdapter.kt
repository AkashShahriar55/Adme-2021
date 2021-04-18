package com.cookietech.namibia.adme.ui.client.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.smarteist.autoimageslider.SliderViewAdapter

class ViewServiceImageSliderAdapter(
    var context: Context,
    private var feature_image_url_list: List<String>
    ): SliderViewAdapter<ViewServiceImageSliderAdapter.ImageSliderViewHolder>() {


    class ImageSliderViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {
        var sliderImage: ImageView? = itemView!!.findViewById(R.id.view_service_image_slider_imageview)

    }

    fun setFeature_image_url_list(feature_image_url_list: List<String>) {
        this.feature_image_url_list = feature_image_url_list
        Toast.makeText(context, "" + feature_image_url_list.size, Toast.LENGTH_LONG).show()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        Log.d("image_count", "getCount: " +  feature_image_url_list.size)
        return feature_image_url_list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ImageSliderViewHolder {
        val inflate = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.layout_view_service_image_slider, null)
        return ImageSliderViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: ImageSliderViewHolder?, position: Int) {
        val feature_image_url = feature_image_url_list[position]

        Glide.with(viewHolder!!.itemView)
            .load(feature_image_url)
            .centerCrop()
            .into(viewHolder.sliderImage!!)
    }
}