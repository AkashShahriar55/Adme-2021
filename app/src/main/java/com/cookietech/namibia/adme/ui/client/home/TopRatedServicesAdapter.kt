package com.cookietech.namibia.adme.ui.client.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.ServicesPOJO

import java.util.ArrayList

class TopRatedServicesAdapter(var topRatedServices : ArrayList<ServicesPOJO>, val context: Context) : RecyclerView.Adapter<TopRatedServicesAdapter.SearchServiceViewHolder>() {



    class SearchServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var iv_profile_pic : ImageView = itemView.findViewById(R.id.iv_profile_pic)
        var tv_service_provider_name : TextView = itemView.findViewById(R.id.tv_service_provider_name)
        var service_rating : RatingBar = itemView.findViewById(R.id.service_rating)
        var tv_rating : TextView = itemView.findViewById(R.id.tv_rating)
        var tv_service_category : TextView = itemView.findViewById(R.id.tv_service_category)
        var tv_distance : TextView = itemView.findViewById(R.id.tv_distance)
        var service_description : TextView = itemView.findViewById(R.id.service_description)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchServiceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_service_item, parent, false)
        return SearchServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchServiceViewHolder, position: Int) {

        val searchData = topRatedServices[position]

        //iv_profile_pic
        holder.tv_service_provider_name.text = searchData.user_name
        holder.service_rating.rating = searchData.rating?.toFloat() ?: 0.0f
        holder.tv_rating.text = searchData.rating.toString()
        holder.tv_service_category.text = searchData.category
        //holder.tv_distance.text =
        holder.service_description.text = searchData.description

        Glide.with(context)
            .load(searchData.pic_url)
            .placeholder(R.mipmap.default_user_photo)
            .into(holder.iv_profile_pic)

    }

    override fun getItemCount(): Int {
        return topRatedServices.size
    }

    fun resetSearchData(newTopRatedServices: ArrayList<ServicesPOJO>){

        topRatedServices.clear()
        topRatedServices.addAll(newTopRatedServices)
        notifyDataSetChanged()

    }
}