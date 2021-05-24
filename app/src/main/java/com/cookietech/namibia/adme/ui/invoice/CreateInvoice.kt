package com.cookietech.namibia.adme.ui.invoice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.appointment.AppointmentViewModel
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.ui.serviceProvider.today.addservice.AddServiceBottomSheetFragment
import kotlinx.android.synthetic.main.activity_create_invoice.*

class CreateInvoice : Fragment(),AddOrEditServiceInvoiceModalDialog.EditServiceCallback {

    lateinit var appointment:AppointmentPOJO
    var itemAdapter: EditInvoiceServiceAdapter? = null
    var itemOrServices:ArrayList<SubServicesPOJO> = ArrayList()
    val viewModel:AppointmentViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_create_invoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_back.setOnClickListener {
            findNavController().navigateUp()
        }

        img_add.setOnClickListener {
            openAddItemModalDialog()
        }


        bt_create.setOnClickListener {
            viewModel.observableFinalServices.value = itemOrServices
            findNavController().navigate(R.id.create_invoice_to_invoice)
        }

        setUpItemRecyclerView()
        initializeObservers()

    }

    private fun openAddItemModalDialog() {
        val bottomSheet = AddOrEditServiceInvoiceModalDialog.newInstance(true,null,this)
        bottomSheet.show(childFragmentManager,"add_or_edit_dialog")
    }

    private fun openEditItemModalDialog(position: Int) {
        if(position < itemOrServices.size ){
            val item = itemOrServices[position]
            val bottomSheet = AddOrEditServiceInvoiceModalDialog.newInstance(false,item,this)
            bottomSheet.show(childFragmentManager,"add_or_edit_dialog")
        }
    }

    private fun initializeObservers() {
        viewModel.observableServices.observe(viewLifecycleOwner) {
            it?.let{services->

                itemOrServices = services
                itemAdapter?.setServiceList(services)
                calculateDiscountValue()
            }
        }
    }

    private fun calculateDiscountValue() {
        var mainPrice = 0.0f;
        for (service in itemOrServices){
            mainPrice += service.quantity * (service.service_charge?.toFloat()?:0.0f)
        }

        viewModel.observableAppointment.value?.let {
            if(it.approved){
                val discountPrice = mainPrice - (it.service_provider_price?.toFloat() ?: 0.0f)
                viewModel.minimumDiscount = discountPrice
            }
        }
    }

    private fun setUpItemRecyclerView() {
        itemAdapter = EditInvoiceServiceAdapter(requireContext(), itemOrServices,object:EditInvoiceServiceAdapter.AddServiceAdapterListener{
            override fun editService(position: Int) {
                openEditItemModalDialog(position)
            }

        })

        rv_edit_invoice.adapter = itemAdapter
        rv_edit_invoice.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onAdd(service: SubServicesPOJO) {
        itemOrServices.add(service)
        itemAdapter?.setServiceList(itemOrServices)
    }

    override fun onEdit(service: SubServicesPOJO) {
        Log.d("callback_debug", "onEdit: " + itemOrServices.contains(service))
        if(itemOrServices.contains(service)){
            itemOrServices[itemOrServices.indexOf(service)] = service
            itemAdapter?.setServiceList(itemOrServices)
        }
    }

    override fun onDelete(service: SubServicesPOJO) {
        Log.d("callback_debug", "onEdit: " + itemOrServices.contains(service))
        if(itemOrServices.contains(service)){
            itemOrServices.remove(service)
            itemAdapter?.setServiceList(itemOrServices)
        }
    }
}