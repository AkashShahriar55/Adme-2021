package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.utils.UiHelper

class AppointmentAdapter(var callback:AppointmentListCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var appointments = ArrayList<AppointmentPOJO>()
    set(value) {
        field =value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val mInflator: LayoutInflater = LayoutInflater.from(parent.context)
        view = mInflator.inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appointment = appointments[position]
        (holder as AppointmentViewHolder).bind(appointment)
         holder.cl_details.setOnClickListener {
             callback.onAppointmentDetailsClicked(appointment)
         }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    internal class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appointment_service = itemView.findViewById<TextView>(R.id.tv_appointment_service)
        var tv_state = itemView.findViewById<TextView>(R.id.tv_service_status)
        var tv_client_name = itemView.findViewById<TextView>(R.id.tv_clint_name)
        var tv_client_address = itemView.findViewById<TextView>(R.id.tv_clint_address)
        var tv_client_text = itemView.findViewById<TextView>(R.id.tv_clint_text)
        var tv_time = itemView.findViewById<TextView>(R.id.rating_time)
        var tv_money = itemView.findViewById<TextView>(R.id.tv_money)
        var cl_details = itemView.findViewById<ConstraintLayout>(R.id.ct_details)


        fun bind(appointment:AppointmentPOJO){
            appointment_service.text = appointment.service_name
            tv_state.text = appointment.state
            tv_client_name.text = appointment.client_name
            tv_client_address.text = appointment.client_address
            tv_client_text.text = appointment.client_quotation
            tv_time.text = UiHelper.getDate(appointment.client_time.toLong(),"dd MMM yyyy, hh:mm aa")
            tv_money.text = "$${appointment.client_price}"
        }
    }


    interface AppointmentListCallback{
        fun onAppointmentDetailsClicked(appointment: AppointmentPOJO)
    }

}