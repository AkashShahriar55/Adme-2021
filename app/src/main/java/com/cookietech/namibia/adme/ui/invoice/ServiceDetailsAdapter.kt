package com.cookietech.namibia.adme.ui.invoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.SubServicesPOJO
import java.util.*
import kotlin.collections.ArrayList

class ServiceDetailsAdapter: RecyclerView.Adapter<ServiceDetailsAdapter.ServiceDetailsViewHolders>() {

    var servicesList = ArrayList<SubServicesPOJO>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceDetailsViewHolders {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.service_list_items, parent, false)
        return ServiceDetailsViewHolders(view)
    }

    override fun onBindViewHolder(holder: ServiceDetailsViewHolders, position: Int) {
        val service= servicesList[position]
        holder.service_name.setText(service.service_name)
        val quantity = "$ ${service.service_charge} x ${service.quantity} ${service.service_unit}"
        holder.service_quantity.text = quantity
        val cost = (service.service_charge?.toFloat()?:0.0f)* service.quantity
        holder.service_cost.text = "$ $cost"
    }

    override fun getItemCount(): Int {
        return servicesList.size
    }

    class ServiceDetailsViewHolders(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var service_name: TextView = itemView.findViewById(R.id.service_name)
        var service_quantity: TextView = itemView.findViewById(R.id.service_quantity)
        var service_cost: TextView = itemView.findViewById(R.id.service_cost)

    }
}