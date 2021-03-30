package com.cookietech.namibia.adme.ui.common.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var cl_view: ConstraintLayout
        private var img_details: ImageView
        private var mv_icon: ImageView
        private var time: TextView
        private var notification_text: TextView

        init {
            notification_text = itemView.findViewById<TextView>(R.id.notification_text)
            time = itemView.findViewById<TextView>(R.id.notification_time)
            mv_icon = itemView.findViewById<ImageView>(R.id.mv_icon)
            img_details = itemView.findViewById<ImageView>(R.id.img_details)
            cl_view = itemView.findViewById(R.id.cl_view)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
       
    }

    override fun getItemCount(): Int {
        return 10
    }
}