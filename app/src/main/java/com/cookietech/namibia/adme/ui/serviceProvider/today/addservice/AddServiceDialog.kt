package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.cookietech.namibia.adme.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.add_service_dialog.*
import java.util.*

class AddServiceDialog(private val calledFrom: String) : AppCompatDialogFragment() {
    private var listener: AdServiceDialogListener? = null
    lateinit var edt_service_name : TextInputLayout
    lateinit var edt_service_description : TextInputLayout
    lateinit var edt_service_charge : TextInputLayout
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.add_service_dialog, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        val cancel_btn = view.findViewById<TextView>(R.id.cancel_btn)
        val add_btn = view.findViewById<TextView>(R.id.add_btn)
        edt_service_name = view.findViewById<TextInputLayout>(R.id.edt_service_name)
         edt_service_description = view.findViewById<TextInputLayout>(R.id.edt_service_description)
        edt_service_charge = view.findViewById<TextInputLayout>(R.id.edt_service_charge)
        cancel_btn.setOnClickListener {
            Log.d("add_service_debug", "cancel_btn: ")
            dialog?.dismiss()
        }

        add_btn.setOnClickListener {
            Log.d("add_service_debug", "add_btn: ")
            val service_name: String =edt_service_name.editText?.text.toString()
            val service_description: String =edt_service_description.editText?.text.toString()
            val service_charge: String =edt_service_charge.editText?.text.toString()
            if (validate(service_name, service_description, service_charge)) {
                listener?.dialogText(service_name, service_description, service_charge)
                dialog?.dismiss()
            }
        }
        return dialog
    }




    override fun onAttach(context: Context) {
        Log.d("add_service_debug", "onAttach: ")
        super.onAttach(context)
        try {
            listener = if (calledFrom == "fragment") {
                targetFragment as AdServiceDialogListener?
            } else {
                activity as AdServiceDialogListener?
            }
        } catch (e: ClassCastException) {
            Log.e("Error AdServiceDialog", ""+e.message)
        }


    }

    private fun validate(
        service_name: String,
        service_description: String,
        service_charge: String
    ): Boolean {
        return if (service_name.isEmpty()) {
            edt_service_name.setErrorEnabled(true)
            edt_service_name.setError("Field can't be empty")
            false
        } else if (service_description.isEmpty()) {
            edt_service_name.setErrorEnabled(false)
            edt_service_description.setErrorEnabled(true)
            edt_service_description.setError("Field can't be empty")
            false
        } else if (service_charge.isEmpty()) {
            edt_service_description.setErrorEnabled(false)
            edt_service_charge.setErrorEnabled(true)
            edt_service_charge.setError("Field can't be empty")
            false
        } else {
            true
        }
    }

    interface AdServiceDialogListener {
        fun dialogText(service_name: String?, service_description: String?, service_charge: String?)
    }
}