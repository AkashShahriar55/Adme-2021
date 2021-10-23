package com.cookietech.namibia.adme.ui.serviceProvider.today

import androidx.recyclerview.widget.DiffUtil
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO

class AppointmentDiffUtilsCallback(private val oldList: ArrayList<AppointmentPOJO>, private val newList: ArrayList<AppointmentPOJO>): DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id &&
                oldList[oldItemPosition].client_ref == newList[newItemPosition].client_ref &&
                oldList[oldItemPosition].service_ref == newList[newItemPosition].service_ref &&
                oldList[oldItemPosition].state == newList[newItemPosition].state &&
                oldList[oldItemPosition].service_provider_ref == newList[newItemPosition].service_provider_ref
    }
}