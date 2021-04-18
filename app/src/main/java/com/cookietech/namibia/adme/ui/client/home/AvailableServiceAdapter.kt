package com.cookietech.namibia.adme.ui.client.home

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.ServiceCategory
import kotlinx.android.synthetic.main.available_service_item.view.*

class AvailableServiceAdapter(val context: Context?,val fromDetails:Boolean):
    RecyclerView.Adapter<AvailableServiceAdapter.AvailableServiceViewHolder>() {

    var categories = ArrayList<ServiceCategory>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    class AvailableServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_icon = itemView.findViewById<ImageView>(R.id.iv_service_icon)
        val tv_service_category = itemView.findViewById<TextView>(R.id.tv_Service_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableServiceViewHolder {
        val view: View
        val mInflator: LayoutInflater = LayoutInflater.from(parent.context)
        view = if(fromDetails){
            mInflator.inflate(R.layout.available_service_item_for_details, parent, false)
        }else{
            mInflator.inflate(R.layout.available_service_item, parent, false)
        }

        return AvailableServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableServiceViewHolder, position: Int) {
        val category = categories[position]

        context?.let {
            Glide.with(it)
                .load(category.icon)
                .into(holder.iv_icon)
        }

        holder.tv_service_category.text = category.category
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}