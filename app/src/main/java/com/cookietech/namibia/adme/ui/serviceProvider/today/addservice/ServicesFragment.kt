package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.today.AddServiceViewModel
import com.cookietech.namibia.adme.models.SubServicesPOJO
import kotlinx.android.synthetic.main.fragment_services.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ServicesFragment : Fragment(),
    AddServiceAdapter.AddServiceAdapterListener {
    private lateinit var addServiceAdapter: AddServiceAdapter


    private var param1: String? = null
    private var param2: String? = null
    val viewmodel: AddServiceViewModel by activityViewModels()

    init {

        Log.d("akash_fragment_debug", "init: ServicesFragment")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("akash_fragment_debug", "onCreate: ServicesFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("akash_fragment_debug", "onCreateView: ServicesFragment")
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("akash_fragment_debug", "onViewCreated: ServicesFragment")

        ad_service_btn.setOnClickListener {
            openDialogFromFragment(null)
        }





        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        ad_service_recyclerView.layoutManager = layoutManager
        ad_service_recyclerView.setHasFixedSize(true)
        addServiceAdapter = AddServiceAdapter(requireContext(), ArrayList(), this)
        ad_service_recyclerView.adapter = addServiceAdapter
        viewmodel.subServicesLiveData.value?.let {
            if (it.size <= 0) {
                ad_service_recyclerView.visibility = View.GONE
                empty_recyclerview_layout.visibility = View.VISIBLE
            }else{
                addServiceAdapter.setServiceList(it)
            }
        } ?: kotlin.run {
            ad_service_recyclerView.visibility = View.GONE
            empty_recyclerview_layout.visibility = View.VISIBLE
        }

        viewmodel.subServicesLiveData.observe(viewLifecycleOwner, { services->
            services?.let { it1 ->
                ad_service_recyclerView.visibility = View.VISIBLE
                empty_recyclerview_layout.visibility = View.GONE
                addServiceAdapter.setServiceList(it1)
            }
        })

    }



    private fun openDialogFromFragment(subServicesPOJO: SubServicesPOJO?) {
        /*val dialog = AddServiceDialog("fragment")
        dialog.setTargetFragment(this, 1)
        dialog.show(parentFragmentManager, "Ad Service Dialog")*/
        val bottomSheet : AddServiceBottomSheetFragment = AddServiceBottomSheetFragment.newInstance(subServicesPOJO)
        bottomSheet.show(childFragmentManager,"add_service_bottom_sheet_fragment")

        bottomSheet.attachSubServiceListener(object :
            AddServiceBottomSheetFragment.SubServiceListener {
            override fun addSubService(subServicesPOJO: SubServicesPOJO) {
                viewmodel.addSubService(subServicesPOJO)
            }

            override fun deleteSubService(subServicesPOJO: SubServicesPOJO?) {

                subServicesPOJO?.let { viewmodel.subServicesLiveData.value?.remove(it) }
                addServiceAdapter.notifyDataSetChanged()
            }

            override fun editSubService() {
                addServiceAdapter.notifyDataSetChanged()

            }

        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ServicesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun deleteService(service: SubServicesPOJO?) {
        openDialogFromFragment(service)
    }
}