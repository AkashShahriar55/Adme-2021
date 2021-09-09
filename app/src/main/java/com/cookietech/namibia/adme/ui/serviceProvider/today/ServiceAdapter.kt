package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.ServicesPOJO
import kotlin.math.log

class ServiceAdapter(var itemClickListener: OnServiceItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var services = ArrayList<ServicesPOJO>()
    set(value) {
        val callback = ServiceDiffUtilsCallback(services,value)
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this)
        field =value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val mInflator: LayoutInflater = LayoutInflater.from(parent.context)
        view = mInflator.inflate(R.layout.services_item_layout, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val service = services[position]
        val mainHolder = holder as ServiceViewHolder
        mainHolder.tv_category.text = service.category
        mainHolder.rb_rating.rating = service.rating.toFloat()
        mainHolder.tv_reviews.text = "0"
        mainHolder.tv_description.text = service.description



        mainHolder.ct_parent.setOnClickListener {
            Log.d("click", "onBindViewHolder: ")
            itemClickListener.onItemClicked(service)
        }
    }

    override fun getItemCount(): Int {
        return services.size
    }

    internal class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var viewServiceButton: Button = itemView.findViewById(R.id.view_service_button)
        var tv_category: TextView = itemView.findViewById(R.id.tv_category)
        var tv_description:TextView = itemView.findViewById<TextView>(R.id.tv_description)
        var tv_reviews:TextView = itemView.findViewById<TextView>(R.id.tv_reviews)
        var rb_rating: RatingBar = itemView.findViewById(R.id.rb_rating)
        var ct_parent: ConstraintLayout = itemView.findViewById(R.id.ct_parent)
    }

    interface OnServiceItemClickListener{
        fun onItemClicked(servicesPOJO: ServicesPOJO)
    }

}