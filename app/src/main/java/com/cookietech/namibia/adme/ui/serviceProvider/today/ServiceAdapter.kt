package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.ServicesPOJO

class ServiceAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var services = ArrayList<ServicesPOJO>()
    set(value) {
        field =value
        notifyDataSetChanged()
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
    }

    override fun getItemCount(): Int {
        return services.size
    }

    internal class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var viewServiceButton: Button
        lateinit var tv_category: TextView
        lateinit var tv_description:TextView
        lateinit var tv_reviews:TextView
        lateinit var rb_rating: RatingBar
        lateinit var ct_parent: ConstraintLayout
        init {
            viewServiceButton = itemView.findViewById(R.id.view_service_button)
            tv_category = itemView.findViewById(R.id.tv_category)
            tv_description = itemView.findViewById<TextView>(R.id.tv_description)
            tv_reviews = itemView.findViewById<TextView>(R.id.tv_reviews)
            rb_rating = itemView.findViewById(R.id.rb_rating)
            ct_parent = itemView.findViewById(R.id.ct_parent)
        }
    }

}