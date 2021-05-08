package com.cookietech.namibia.adme.ui.client.home

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.SubServicesPOJO

class SelectServiceAdapter : RecyclerView.Adapter<SelectServiceAdapter.SelectServiceViewHolder?> {
    private var context: Context? = null
    var SelectServiceList = arrayListOf<SubServicesPOJO>()
    set(value) {
        field= value
        notifyDataSetChanged()
    }
    private var listener: SelectServiceAdapterListener? = null

    interface SelectServiceAdapterListener {
        fun onSelectServiceSelected(selectServiceItem: SubServicesPOJO)
    }



    internal constructor() {}
    constructor(
        context: Context?,
        listener: SelectServiceAdapterListener?
    ) {
        this.context = context
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectServiceViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.select_service_item, parent, false)
        return SelectServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectServiceViewHolder, position: Int) {
        val selectServiceItem = SelectServiceList[position]
        Log.d("service_debug", "onBindViewHolder: " + selectServiceItem.quantity)
        holder.tv_service_title.setText(selectServiceItem.service_name)
        holder.tv_service_details.setText(selectServiceItem.service_description)
        holder.tv_service_price.text = selectServiceItem.service_charge+"/"+selectServiceItem.service_unit
    }

    override fun getItemCount(): Int {
        return SelectServiceList!!.size
        //        return 3;
    }

    inner class SelectServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_service_title: TextView = view.findViewById(R.id.tv_service_title)
        var tv_service_details: TextView = view.findViewById(R.id.tv_service_details)
        var tv_service_price: TextView = view.findViewById(R.id.tv_service_price)
        var tv_plus_button: ImageView = view.findViewById(R.id.plus_service)
        var tv_minus_button: ImageView = view.findViewById(R.id.minus_service)

        init {

//            Typeface tf0 = Typeface.createFromAsset(context.getAssets(), "fonts/Sansation-Regular.ttf");
//            current.setTypeface(tf0);

            tv_minus_button.alpha = 0.5f
            tv_minus_button.setOnClickListener {
                val service = SelectServiceList[adapterPosition]
                if(service.quantity == 0){
                    return@setOnClickListener
                }
                service.quantity = service.quantity-1
                if(service.quantity == 0){
                    tv_minus_button.alpha = 0.5f
                }
                SelectServiceList[adapterPosition] = service
                listener!!.onSelectServiceSelected(SelectServiceList[adapterPosition])
            }

            tv_plus_button.setOnClickListener {
                val service = SelectServiceList[adapterPosition]
                if(service.quantity == 0){
                    tv_minus_button.alpha = 1f
                }
                service.quantity = service.quantity+1
                SelectServiceList[adapterPosition] = service
                listener!!.onSelectServiceSelected(SelectServiceList[adapterPosition])
            }
        }
    }
}