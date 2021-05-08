package com.cookietech.namibia.adme.ui.invoice

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.ui.serviceProvider.today.addservice.AddServiceBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_add_edit_invoice_item.*
import kotlinx.android.synthetic.main.otp_bottom_sheet_dialog.*

class AddOrEditServiceInvoiceModalDialog: BottomSheetDialogFragment()  {

    private var mListener: EditServiceCallback?= null
    private var service: SubServicesPOJO = SubServicesPOJO()
    private var isAdd: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_add_edit_invoice_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.apply {
            isAdd = getBoolean("is_add")
            getParcelable<SubServicesPOJO>("service")?.let {
                service = it
            }
        }

        setUpViews()
        setupClicks()
    }


    interface EditServiceCallback{
        fun onAdd(service:SubServicesPOJO)
        fun onEdit(service: SubServicesPOJO)
        fun onDelete(service: SubServicesPOJO)
    }

    private fun setupClicks() {

    }

    private fun setUpViews() {

        if(isAdd){
            bt_delete.visibility = View.GONE
            edt_invoice_service_unit.setText("Unit")
            edt_invoice_service_count.setText("1")
            bt_done.setOnClickListener {
                validateAddServiceAndFinish()
            }

        }else{
            tv_dialog_title.text = "Edit/Delete Service or item"
            edt_invoice_service_money.hint = "Per ${service.service_unit}"
            til_service_unit.visibility = View.GONE
            edt_invoice_service_title.setText(service.service_name)
            edt_invoice_service_money.setText(service.service_charge)
            edt_invoice_service_count.setText(service.quantity.toString())

            bt_done.setOnClickListener {
                validateEditServiceAndFinish()
            }

            bt_delete.setOnClickListener {
                mListener?.onDelete(service)
                dismiss()
            }
        }
        

    }

    private fun validateAddServiceAndFinish() {
        val name = edt_invoice_service_title.text.toString()
        if(name.isEmpty()){
            edt_invoice_service_title.error = "Please fill this up"
            edt_invoice_service_title.requestFocus()
            return
        }

        val unit = edt_invoice_service_unit.text.toString()
        if(unit.isEmpty()){
            edt_invoice_service_unit.error = "Please fill this up"
            edt_invoice_service_unit.requestFocus()
            return
        }

        val price = edt_invoice_service_money.text.toString()
        if(price.isEmpty()){
            edt_invoice_service_money.error = "Please fill this up"
            edt_invoice_service_money.requestFocus()
            return
        }

        val quantity = edt_invoice_service_count.text.toString()
        if(quantity.isEmpty()){
            edt_invoice_service_count.error = "Please fill this up"
            edt_invoice_service_count.requestFocus()
            return
        }

        service.service_name = name
        service.service_unit = unit
        service.service_charge = price
        service.quantity = quantity.toInt()

        mListener?.onAdd(service)
        dismiss()
    }

    private fun validateEditServiceAndFinish() {
        val name = edt_invoice_service_title.text.toString()
        if(name.isEmpty()){
            edt_invoice_service_title.error = "Please fill this up"
            edt_invoice_service_title.requestFocus()
            return
        }


        val price = edt_invoice_service_money.text.toString()
        if(price.isEmpty()){
            edt_invoice_service_money.error = "Please fill this up"
            edt_invoice_service_money.requestFocus()
            return
        }

        val quantity = edt_invoice_service_count.text.toString()
        if(quantity.isEmpty()){
            edt_invoice_service_count.error = "Please fill this up"
            edt_invoice_service_count.requestFocus()
            return
        }

        service.service_name = name
        service.service_charge = price
        service.quantity = quantity.toInt()

        mListener?.onEdit(service)
        dismiss()

    }

    companion object {
        @JvmStatic
        fun newInstance(isAdd:Boolean,service:SubServicesPOJO?,callback: EditServiceCallback): AddOrEditServiceInvoiceModalDialog {
            val fragment = AddOrEditServiceInvoiceModalDialog()
            val bundle = Bundle()
            bundle.putBoolean("is_add",isAdd)
            bundle.putParcelable("service",service)
            fragment.mListener = callback
            fragment.arguments = bundle
            return fragment
        }
    }

}