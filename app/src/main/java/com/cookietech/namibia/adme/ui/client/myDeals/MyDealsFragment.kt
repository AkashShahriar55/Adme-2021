package com.cookietech.namibia.adme.ui.client.myDeals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
class MyDealsFragment : Fragment() {
    private lateinit var adapter: AppointmentAdapter

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
    }

    val appointmentObserver = Observer<ArrayList<AppointmentPOJO>>{appointments->
        appointment_shimmer_holder.stopShimmerAnimation()
        appointment_shimmer_holder.visibility = View.GONE
        adapter.appointments = appointments
    }

    private fun initializeObserver() {
        viewModel.observableAppointments.observe(viewLifecycleOwner,appointmentObserver)
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