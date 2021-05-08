package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.Toast
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.add_service_dialog.*

class AddServiceBottomSheetFragment : BottomSheetDialogFragment() {

    private var subServiceListener : SubServiceListener? = null
    private  var subServicesPOJO:  SubServicesPOJO? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_service_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setupClicks()
    }

    private fun setupClicks() {
        bt_ok.setOnClickListener {


            if(edt_service_tittle.text.isNullOrEmpty() || edt_service_description.text.isNullOrEmpty() || edt_price.text.isNullOrEmpty() || edt_unit.text.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Fields Can't be empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
               if (subServicesPOJO == null){
                   val subServicesPOJO = SubServicesPOJO(edt_service_tittle.text.toString().trim(),edt_service_description.text.toString().trim(), edt_price.text.toString().trim(), edt_unit.text.toString().trim())
                   subServiceListener?.addSubService(subServicesPOJO)
               } else{
                   subServicesPOJO!!.service_name = edt_service_tittle.text.toString().trim()
                   subServicesPOJO!!.service_description = edt_service_description.text.toString().trim()
                   subServicesPOJO!!.service_charge = edt_price.text.toString().trim()
                   subServicesPOJO!!.service_unit =  edt_unit.text.toString().trim()
                   subServiceListener?.editSubService()

               }
                dismiss()
            }
        }

        bt_delete.setOnClickListener {

            if (subServicesPOJO != null){
                subServiceListener?.deleteSubService(subServicesPOJO)
            }

            dismiss()
        }
    }

    private fun setUpViews() {
        /**AutoComplete textView**/
        /*val edt_unit : AutoCompleteTextView = view.findViewById(R.id.edt_unit)
        val units : Array<out String> = resources.getStringArray(R.array.units)
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, units)
        edt_unit.setAdapter(adapter)*/

        if (subServicesPOJO !=null){
            edt_service_tittle.setText(subServicesPOJO!!.service_name)
            edt_service_description.setText(subServicesPOJO!!.service_description)
            edt_price.setText(subServicesPOJO!!.service_charge)
            edt_unit.setText(subServicesPOJO!!.service_unit)
            bt_ok.text = "Save"
            bt_delete.text = "Delete"
        }
    }


    override fun setupDialog(dialog: Dialog, style: Int) {
        val rootView = View.inflate(context, R.layout.add_service_dialog, null)
        dialog.setContentView(rootView)
        dialog.setCanceledOnTouchOutside(false)

        val bottomSheet = dialog.window?.findViewById(R.id.design_bottom_sheet) as FrameLayout
        val behaviour = BottomSheetBehavior.from(bottomSheet)

        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun attachSubServiceListener(subServiceListener: SubServiceListener){
        this.subServiceListener = subServiceListener
    }

    companion object {
        @JvmStatic
        fun newInstance(subServicesPOJO: SubServicesPOJO?): AddServiceBottomSheetFragment {
            val fragment = AddServiceBottomSheetFragment()
            fragment.subServicesPOJO = subServicesPOJO
            /*fragment.postId = post_id
            fragment.commentCount = commentCount*/
            return fragment
        }
    }

    interface SubServiceListener{
        fun addSubService(subServicesPOJO: SubServicesPOJO)
        fun deleteSubService(subServicesPOJO: SubServicesPOJO?)
        fun editSubService()
    }
}