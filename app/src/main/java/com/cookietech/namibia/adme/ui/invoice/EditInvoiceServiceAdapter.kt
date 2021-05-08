package com.cookietech.namibia.adme.ui.invoice

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


class EditInvoiceServiceAdapter internal constructor(
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
        view = inflater.inflate(R.layout.add_service_item, parent, false)
        return EditInvoiceServiceViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val service = serviceList[position]
        val serviceHolder = holder as EditInvoiceServiceViewHolder
        serviceHolder.tv_service_title.text = service.service_name
        serviceHolder.tv_service_details.text = "$${service.service_charge} x ${service.quantity} ${service.service_unit}"
        val totalPrice = (service.service_charge?.toFloat() ?: 0.0f) * service.quantity
        serviceHolder.tv_service_price.text = "$$totalPrice"
        serviceHolder.itemView.setOnClickListener(View.OnClickListener {
            listener.editService(
                position
            )
        })
        serviceHolder.tv_service_button.text = "Edit"
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    internal class EditInvoiceServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        fun editService(position: Int)
    }
}