package com.cookietech.namibia.adme.ui.serviceProvider.today.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.SubServicesPOJO

class AppointmentServicesAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var subServices = ArrayList<SubServicesPOJO>()
    set(value) {
        field =value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val mInflator: LayoutInflater = LayoutInflater.from(parent.context)
        view = mInflator.inflate(R.layout.appointment_sub_service_item, parent, false)
        return AppointmentSubServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val service = subServices[position]
        (holder as AppointmentSubServiceViewHolder).bind(service)
    }

    override fun getItemCount(): Int {
        return subServices.size
    }

    internal class AppointmentSubServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_sub_service_name = itemView.findViewById<TextView>(R.id.tv_sub_service_name)
        var tv_sub_service_price = itemView.findViewById<TextView>(R.id.tv_sub_service_price)


        fun bind(subService:SubServicesPOJO){
            tv_sub_service_name.text = subService.service_name +" x "+subService.quantity+" "+subService.service_unit
            val charge = (subService.service_charge?.toFloat() ?: 0f) * subService.quantity
            tv_sub_service_price.text = charge.toString()
        }
    }

}