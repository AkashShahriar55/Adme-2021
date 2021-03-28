package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R

class ServiceAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val mInflator: LayoutInflater = LayoutInflater.from(parent.context)
        view = mInflator.inflate(R.layout.services_item_layout, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 5
    }

    internal class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

        }
    }

}