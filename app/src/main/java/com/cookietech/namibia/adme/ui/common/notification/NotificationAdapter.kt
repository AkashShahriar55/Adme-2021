package com.cookietech.namibia.adme.ui.common.notification

import android.app.Notification
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.helper.TimeHelper
import com.cookietech.namibia.adme.models.NotificationPOJO

class NotificationAdapter(
    var notificationClickListener: NotificationClickListener,
    var context: Context
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    var notificationList = ArrayList<NotificationPOJO>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cl_view: ConstraintLayout = itemView.findViewById(R.id.cl_view)
        var img_details: ImageView = itemView.findViewById<ImageView>(R.id.img_details)
        var mv_icon: ImageView = itemView.findViewById<ImageView>(R.id.mv_icon)
        var time: TextView = itemView.findViewById<TextView>(R.id.notification_time)
        var notification_text: TextView = itemView.findViewById<TextView>(R.id.notification_text)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val notification = notificationList[position]
        holder.notification_text.text = notification.text
        holder.time.text = notification.time?.let { TimeHelper.getTimeDifference(it) }

        Glide.with(context)
            .asBitmap()
            .load(notification.img_url)
            .into(holder.mv_icon)

        holder.cl_view.setOnClickListener{
            if (notification.type.equals("quotation")){
                notification.reference?.let { it1 ->
                    notificationClickListener.onNotificationClicked(it1)
                }
            }
        }
       
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    interface NotificationClickListener{

        fun onNotificationClicked(appointmentId: String)
    }
}