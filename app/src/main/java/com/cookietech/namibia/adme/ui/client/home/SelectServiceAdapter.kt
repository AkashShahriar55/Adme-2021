package com.cookietech.namibia.adme.ui.client.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        holder.tv_service_title.setText(selectServiceItem.service_name)
        holder.tv_service_details.setText(selectServiceItem.service_description)
        holder.tv_service_price.setText(selectServiceItem.service_charge)
    }

    override fun getItemCount(): Int {
        return SelectServiceList!!.size
        //        return 3;
    }

    inner class SelectServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_service_title: TextView = view.findViewById(R.id.tv_service_title)
        var tv_service_details: TextView = view.findViewById(R.id.tv_service_details)
        var tv_service_price: TextView = view.findViewById(R.id.tv_service_price)
        var tv_service_button: TextView = view.findViewById(R.id.tv_service_button)

        init {

//            Typeface tf0 = Typeface.createFromAsset(context.getAssets(), "fonts/Sansation-Regular.ttf");
//            current.setTypeface(tf0);
            view.setOnClickListener {
                listener!!.onSelectServiceSelected(SelectServiceList[adapterPosition])
                if (tv_service_button.text == "Add Service") {
                    tv_service_button.setTextColor(Color.RED)
                    tv_service_button.text = "Remove Service"
                } else {
                    tv_service_button.setTextColor(Color.parseColor("#3F5AA6"))
                    tv_service_button.setText("Add Service")
                }
            }
        }
    }
}