package com.cookietech.namibia.adme.ui.serviceProvider.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R

class LeaderBoardAdapter : RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>() {


    class LeaderBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var profile_image: ImageView
        private var service_charge: TextView
        private var service_type: TextView
        private var service_name: TextView
        private var username: TextView

        init {
            username = itemView.findViewById(R.id.username)
            service_name = itemView.findViewById(R.id.service_name)
            service_type = itemView.findViewById(R.id.service_type)
            service_charge = itemView.findViewById(R.id.service_charge)
            profile_image = itemView.findViewById(R.id.profile_image)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_item, parent, false)

        return LeaderBoardViewHolder(view)


    }

    override fun onBindViewHolder(holder: LeaderBoardViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {

        return 10

    }
}