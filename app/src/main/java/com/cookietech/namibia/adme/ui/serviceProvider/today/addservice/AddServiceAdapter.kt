package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.SubServicesPOJO


class AddServiceAdapter internal constructor(
    private val context: Context,
    private var serviceList: ArrayList<SubServicesPOJO>,
    private val listener: AddServiceAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    fun setServiceList(serviceList: ArrayList<SubServicesPOJO>) {
        this.serviceList = serviceList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View
        Log.d("akash-debug", "onCreateViewHolder: ")
        view = inflater.inflate(R.layout.select_service_item, parent, false)
        return AddServiceViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val service = serviceList[position]
        val serviceHolder = holder as AddServiceViewHolder
        serviceHolder.tv_service_title.setText(service.service_name)
        serviceHolder.tv_service_details.setText(service.service_description)
        serviceHolder.tv_service_price.setText("$" + service.service_charge)
        serviceHolder.tv_service_button.setOnClickListener(View.OnClickListener {
            listener.deleteService(
                position
            )
        })
        serviceHolder.tv_service_button.text = "Delete"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            serviceHolder.tv_service_button.setTextColor(
                context.resources.getColor(
                    R.color.color_negative,
                    null
                )
            )
        } else {
            serviceHolder.tv_service_button.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    internal class AddServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_service_title: TextView
        var tv_service_details: TextView
        var tv_service_price: TextView
        var tv_service_button: TextView

        init {
            tv_service_title = itemView.findViewById<TextView>(R.id.tv_service_title)
            tv_service_details = itemView.findViewById<TextView>(R.id.tv_service_details)
            tv_service_price = itemView.findViewById<TextView>(R.id.tv_service_price)
            tv_service_button = itemView.findViewById<TextView>(R.id.tv_service_button)
        }
    }

    interface AddServiceAdapterListener {
        fun deleteService(position: Int)
    }
}