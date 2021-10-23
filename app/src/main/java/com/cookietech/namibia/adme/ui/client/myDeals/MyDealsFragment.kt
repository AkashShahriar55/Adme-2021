package com.cookietech.namibia.adme.ui.client.myDeals

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.Application.Status
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.client.myDeals.MyDealsViewModel
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.ui.serviceProvider.today.AppointmentAdapter
import kotlinx.android.synthetic.main.fragment_my_deals.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyDealsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val ALL_DEALS ="all_deals"
const val ACTIVE_DEALS = "active_deals"
const val COMPLETED_DEALS = "completed_deals"
const val CANCELLED_DEALS = "cancelled_deals"
class MyDealsFragment : Fragment() {
    private var allAppointments: ArrayList<AppointmentPOJO>? = null
    private lateinit var adapter: AppointmentAdapter

    private var filterIndex = ALL_DEALS



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val viewModel:MyDealsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_deals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appointment_shimmer_holder.startShimmerAnimation()
        initializeRecyclerView()
        initializeObserver()
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    }

    val appointmentObserver = Observer<ArrayList<AppointmentPOJO>>{appointments->
        appointment_shimmer_holder.stopShimmerAnimation()
        appointment_shimmer_holder.visibility = View.GONE
        allAppointments = appointments
        when(filterIndex){
            ALL_DEALS->{
                adapter.appointments = appointments
            }
            ACTIVE_DEALS->{
                adapter.appointments = appointments.filter { it.state !in arrayOf(Status.status_client_request_cancel,Status.status_provider_request_cancel,Status.status_payment_completed) } as ArrayList<AppointmentPOJO>
            }
            COMPLETED_DEALS->{
                adapter.appointments = appointments.filter { it.state in arrayOf(Status.status_payment_completed) } as ArrayList<AppointmentPOJO>
            }
            CANCELLED_DEALS->{
                adapter.appointments = appointments.filter { it.state in arrayOf(Status.status_client_request_cancel,Status.status_provider_request_cancel) } as ArrayList<AppointmentPOJO>
            }
        }

    }

    private fun initializeObserver() {
        viewModel.observableAppointments.observe(viewLifecycleOwner,appointmentObserver)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        toolbar.overflowIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_filter)
        inflater.inflate(R.menu.filter_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        allAppointments?.let {deals->
            when(item.itemId){
                R.id.all_deals->{
                    filterIndex = ALL_DEALS
                    toolbar.title = "All Deals"
                    adapter.appointments = deals
                }
                R.id.active_deals->{
                    filterIndex = ACTIVE_DEALS
                    toolbar.title = "Active Deals"
                    adapter.appointments = deals.filter { it.state !in arrayOf(Status.status_client_request_cancel,Status.status_provider_request_cancel,Status.status_payment_completed) } as ArrayList<AppointmentPOJO>
                }
                R.id.completed_deals->{
                    filterIndex = COMPLETED_DEALS
                    toolbar.title = "Completed Deals"
                    adapter.appointments = deals.filter { it.state in arrayOf(Status.status_payment_completed) } as ArrayList<AppointmentPOJO>
                }
                R.id.canceled_deals->{
                    filterIndex = CANCELLED_DEALS
                    toolbar.title = "Canceled Deals"
                    adapter.appointments = deals.filter { it.state in arrayOf(Status.status_client_request_cancel,Status.status_provider_request_cancel) } as ArrayList<AppointmentPOJO>
                }
            }
        }

        return super.onOptionsItemSelected(item)

    }



    override fun onStart() {
        super.onStart()
        initializeObserver()
    }

    override fun onStop() {
        super.onStop()
        removeObserver()
    }

    private fun removeObserver() {
        viewModel.observableAppointments.removeObserver(appointmentObserver)

    }

    private fun initializeRecyclerView() {
        client_appointment_rv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter = AppointmentAdapter(object :AppointmentAdapter.AppointmentListCallback{
            override fun onAppointmentDetailsClicked(appointment: AppointmentPOJO) {
                val bundle = Bundle()
                bundle.putParcelable("appointment",appointment)
                bundle.putString("appointment_id",null)
                findNavController().navigate(R.id.my_deals_to_appointment_details,bundle)
            }

        })

        client_appointment_rv.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyDealsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyDealsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}