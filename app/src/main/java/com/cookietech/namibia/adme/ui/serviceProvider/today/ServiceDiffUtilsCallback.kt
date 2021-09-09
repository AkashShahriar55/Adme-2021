package com.cookietech.namibia.adme.ui.serviceProvider.today

import androidx.recyclerview.widget.DiffUtil
import com.cookietech.namibia.adme.models.ServicesPOJO

class ServiceDiffUtilsCallback(private val oldList: ArrayList<ServicesPOJO>, val newList: ArrayList<ServicesPOJO>): DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].mServiceId == newList[newItemPosition].mServiceId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].mServiceId == newList[newItemPosition].mServiceId &&
                oldList[oldItemPosition].user_ref == newList[newItemPosition].user_ref &&
                oldList[oldItemPosition].category == newList[newItemPosition].category
    }
}