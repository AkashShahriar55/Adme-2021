package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.ServiceProviderViewModel
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.utils.UiHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.layout_empty_recycleview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayFragment : Fragment(), OnMapReadyCallback {
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var oldPeekHeight: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var isbottomSheetVisible = false
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mMap: GoogleMap? = null

    val serviceProviderViewModel:ServiceProviderViewModel by activityViewModels()
    var serviceProviderPOJO:ServiceProviderPOJO? = null
    lateinit var servicesAdapter: ServiceAdapter

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
        return inflater.inflate(R.layout.fragment_today, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        initializeObservers()
        initializeClicks()
    }

    private fun initializeClicks() {

        client_notification_btn.setOnClickListener{
            findNavController().navigate(R.id.today_to_notification)
        }
    }

    private fun initializeObservers() {
        serviceProviderViewModel.createOrFetchServiceData(object : ServiceProviderDataCallback {
            override fun onCreateSuccessful() {

            }

            override fun onFetchSuccessful() {

            }

            override fun onCreateOrFetchFailed(exception: Exception) {

            }

        })
        serviceProviderViewModel.service_provider_data.observe(viewLifecycleOwner,
            { data->
                serviceProviderPOJO = data
                updateView()
            })
    }

    private fun setUpMap() {
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@TodayFragment)
        }


    }

    private fun initializeViews() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_details)
        oldPeekHeight = bottomSheetBehavior.peekHeight
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        if (isbottomSheetVisible) {
                            UiHelper(requireContext()).setMargins(
                                today_location_button,
                                0,
                                0,
                                0,
                                60
                            )
                            today_location_button.requestLayout()
                            bottomSheetBehavior.setPeekHeight(
                                bottom_details_toolbar.height + 5,
                                true
                            )
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        setUpMap()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TodayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TodayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        updateMarkers()
    }

    private fun updateMarkers() {
        FirebaseManager.currentUser?.apply {
            val lat = lattitude?.toDoubleOrNull()
            val lng = longitude?.toDoubleOrNull()
            val currentLocation = lat?.let {latt->
                lng?.let {lngg->
                    LatLng(latt, lngg)
                }
            }

            mMap?.clear()
            mMap?.addMarker(
                currentLocation?.let {
                    MarkerOptions().position(it)
                        .title("your location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker))
                }
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f));

        }
    }

    private fun updateView() {
        mainScope.launch {
            UiHelper(requireContext()).setMargins(today_location_button, 0, 0, 0, 180)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            isbottomSheetVisible = true
        }

        serviceProviderPOJO?.apply {
            tv_total_income.text = total_income.toString()
            tv_income_today.text = monthly_income.toString()
            tv_due.text = monthly_due.toString()
            tv_pressed_today.text = pressed.toString()
            tv_requested_today.text = requested.toString()
            tv_completed_today.text = completed.toString()
        }

        servicesAdapter = ServiceAdapter()
        service_recyclerview.visibility = View.VISIBLE
        empty_recyclerview.visibility = View.GONE
        service_recyclerview.layoutManager = LinearLayoutManager(context)
        service_recyclerview.adapter = servicesAdapter

    }
}