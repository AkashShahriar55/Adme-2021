package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.ServiceProviderViewModel
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServiceProviderPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.utils.UiHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.bottom_details
import kotlinx.android.synthetic.main.fragment_today.bottom_details_back
import kotlinx.android.synthetic.main.fragment_today.bottom_details_toolbar
import kotlinx.android.synthetic.main.fragment_today.client_notification_btn
import kotlinx.android.synthetic.main.fragment_today.today_notification_badge
import kotlinx.android.synthetic.main.layout_empty_recycleview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TodayFragment : Fragment(), OnMapReadyCallback {
    private var isMarkerSet: Boolean = false
    private lateinit var appointmentAdapter: AppointmentAdapter
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var oldPeekHeight: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var isbottomSheetVisible = false
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mMap: GoogleMap? = null

    var closestMarker:Marker? = null

    val serviceProviderViewModel:ServiceProviderViewModel by activityViewModels()
    var serviceProviderPOJO:ServiceProviderPOJO? = null

    val serviceDataObserver =  Observer<ServiceProviderPOJO?> { data ->
        serviceProviderPOJO = data
        updateView()
        startPostponedEnterTransition()
    }

    val servicesObserver = Observer<ArrayList<ServicesPOJO>> { services ->
        Log.d("database_debug", "initializeObservers:  ${services.size}")
        if (!services.isNullOrEmpty()) {
            servicesAdapter.services = services
            service_recyclerview.visibility = View.VISIBLE
            empty_recyclerview.visibility = View.GONE
            service_shimmer_holder.stopShimmerAnimation()
            service_shimmer_holder.visibility = View.GONE
        } else {
            service_recyclerview.visibility = View.GONE
            empty_recyclerview.visibility = View.VISIBLE
            service_shimmer_holder.stopShimmerAnimation()
            service_shimmer_holder.visibility = View.GONE
        }
    }

    val appointmentsObserver = Observer<ArrayList<AppointmentPOJO>> { appointments->
        if (!appointments.isNullOrEmpty()) {
            appointmentAdapter.appointments = appointments
            appointment_container.visibility = View.VISIBLE
            empty_recyclerview_appointment.visibility = View.GONE
            updateAppointmentMarkers(appointments)
            appointment_shimmer_holder.stopShimmerAnimation()
            appointment_shimmer_holder.visibility = View.GONE
        } else {
            appointment_container.visibility = View.GONE
            empty_recyclerview_appointment.visibility = View.VISIBLE
            appointment_shimmer_holder.stopShimmerAnimation()
            appointment_shimmer_holder.visibility = View.GONE
        }
    }



    lateinit var servicesAdapter: ServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("life_cycle", "onCreate: $savedInstanceState")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setUpMap()

        serviceProviderViewModel.createOrFetchServiceData(object : ServiceProviderDataCallback {
            override fun onCreateSuccessful() {

            }

            override fun onFetchSuccessful() {

            }

            override fun onCreateOrFetchFailed(exception: Exception) {

            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("life_cycle", "onCreateView: ")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("life_cycle", "onViewCreated: " + savedInstanceState)
        initializeViews()
        initializeClicks()
        postponeEnterTransition()
    }


    private fun initializeClicks() {

        client_notification_btn.setOnClickListener{
            findNavController().navigate(R.id.today_to_notification)
        }

        today_add_service.setOnClickListener {
            val action = TodayFragmentDirections.todayToAddService(null)
            findNavController().navigate(action)
        }

        today_location_button.setOnClickListener{
            FirebaseManager.currentUser?.let { user->
                mMap?.apply {
                    val lat = user.lattitude?.toDoubleOrNull()
                    val lng = user.longitude?.toDoubleOrNull()
                    val currentLocation = lat?.let { latitude-> lng?.let { longitude -> LatLng(
                        latitude,
                        longitude
                    ) } }
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f))
                }
            }
        }

    }


    override fun onStart() {
        super.onStart()
        Log.d("life_cycle", "onStart: ")
        initializeObservers()
    }

    override fun onPause() {
        super.onPause()
        Log.d("life_cycle", "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
        Log.d("life_cycle", "onStop: ")
    }

    private fun removeObservers() {
        serviceProviderViewModel.service_provider_data.removeObserver(
            serviceDataObserver)
        serviceProviderViewModel.services.removeObserver( servicesObserver)
        serviceProviderViewModel.observableAppointments.removeObserver(appointmentsObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("life_cycle", "onDestroy: ")
    }
    private fun initializeObservers() {
        serviceProviderViewModel.service_provider_data.observe(viewLifecycleOwner,
            serviceDataObserver)

    }


    private val markers = arrayListOf<Marker?>()
    private val markerMaps = hashMapOf<Marker?,AppointmentPOJO?>()
    private fun updateAppointmentMarkers(appointments: ArrayList<AppointmentPOJO>) {

        var closestTime = Long.MAX_VALUE
        var isNewClosest = false

        for (appointment in appointments) {

            if(markerMaps.values.contains(appointment)){
                Log.d("marker_debug", "updateAppointmentMarkers: " + appointment.client_quotation)
                continue
            }


            val latitude= appointment.client_latitude?.toDouble()
            val longitude = appointment.client_longitude?.toDouble()

            val timeDistance = appointment.client_time.toLong() - System.currentTimeMillis();
            Log.d("time_debug", "updateMapMarkers: $timeDistance")

            if( closestTime > timeDistance){
                closestTime = timeDistance
                isNewClosest = true
            }else{
                isNewClosest = false
            }

            val position = latitude?.let { lat->
                longitude?.let { lng ->
                    LatLng(
                        lat, lng
                    )
                }
            }

            Glide.with(requireContext())
                .asBitmap()
                .load(appointment.client_profile_pic)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("bitmap_debug", "onLoadFailed: ${appointment.client_profile_pic}")
                        val markerBitmap = serviceProviderViewModel.generateMarkerBitmap(requireContext(),
                            BitmapFactory.decodeResource(
                            resources, R.drawable.profile
                        ))
                        addMarker(markerBitmap,position,appointment,isNewClosest)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("bitmap_debug", "onResourceReady: ${appointment.client_profile_pic}")
                        val markerBitmap = if (resource != null) {
                            serviceProviderViewModel.generateMarkerBitmap(requireContext(), resource)
                        } else {
                            serviceProviderViewModel.generateMarkerBitmap(requireContext(), BitmapFactory.decodeResource(
                                resources, R.drawable.profile
                            ))

                        }
                        addMarker(markerBitmap, position, appointment, isNewClosest)

                        return false
                    }

                })
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.d("marker_debug", "onLoadCleared: image asheni ")
                    }

                })


        }



    }

    fun addMarker(
        markerBitmap: Bitmap?,
        position: LatLng?,
        id: AppointmentPOJO?,
        isNewClosest: Boolean
    ){
        val marker = mMap?.addMarker(
            MarkerOptions().position(position!!).icon(
                BitmapDescriptorFactory.fromBitmap(
                    markerBitmap
                )
            )
        )
        markers.add(marker)
        markerMaps[marker] = id
        if(isNewClosest)
            closestMarker = marker;
    }

    private fun setUpMap() {
        isMarkerSet = false
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@TodayFragment)
        }


    }

    private fun initializeViews() {

        if(FirebaseManager.currentUser?.hasUnreadNotifSP == true){
            today_notification_badge.visibility = View.VISIBLE
        }
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
                    BottomSheetBehavior.STATE_EXPANDED->{
                        serviceProviderViewModel.services.observe(viewLifecycleOwner, servicesObserver)
                        serviceProviderViewModel.observableAppointments.observe(viewLifecycleOwner,appointmentsObserver)
                        ObjectAnimator.ofFloat(bottom_details_back, View.ROTATION, bottom_details_back.rotation, -90f).setDuration(100).start();
                        bottom_details_back.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED->{
                        ObjectAnimator.ofFloat(bottom_details_back, View.ROTATION, bottom_details_back.rotation, 0f).setDuration(100).start();
                        bottom_details_back.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


    }

    companion object {

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

        mMap?.setOnInfoWindowClickListener {
            val appointment = markerMaps.get(it)
            appointment?.let {
                val bundle = Bundle()
                bundle.putParcelable("appointment",appointment)
                bundle.putString("appointment_id",null)
                findNavController().navigate(R.id.today_to_appointment_activity,bundle)
            }
        }
        mMap?.setInfoWindowAdapter(object :GoogleMap.InfoWindowAdapter{
            @SuppressLint("PotentialBehaviorOverride")
            override fun getInfoWindow(p0: Marker): View? {
                val view =  layoutInflater.inflate(R.layout.appointment_map_info,null)
                val appointment = markerMaps.get(p0)
                appointment?.let {
                    val category = view.findViewById<TextView>(R.id.tv_appointment_service)
                    val client_name = view.findViewById<TextView>(R.id.tv_clint_name)
                    val client_address = view.findViewById<TextView>(R.id.tv_clint_address)
                    val price = view.findViewById<TextView>(R.id.tv_money)
                    val container = view.findViewById<ConstraintLayout>(R.id.appointment_main_layout)
                    category.text = it.service_name
                    client_name.text = it.client_name
                    client_address.text = it.client_address

                    it.client_price?.let {money->
                        price.text = "$$money"
                    }

                    it.service_provider_price?.let {money->
                        price.text = "$$money"
                    }
                    




                }
                return view
            }

            override fun getInfoContents(p0: Marker): View? {
               return null
            }

        })
        updateMarkers()
        serviceProviderViewModel.observableAppointments.value?.apply {
            Log.d("marker_debug", "initializeObservers: service asche $size $isMarkerSet")
            if(!isMarkerSet)
                updateAppointmentMarkers(this)
        }
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

            serviceProviderPOJO?.apply {
                tv_total_income.text = total_income.toString()
                tv_income_today.text = monthly_income.toString()
                tv_due.text = monthly_due.toString()
                tv_pressed_today.text = pressed.toString()
                tv_requested_today.text = requested.toString()
                tv_completed_today.text = completed.toString()
            }


            service_recyclerview.layoutManager = LinearLayoutManager(context)
            service_recyclerview.adapter = servicesAdapter



            appointment_container.layoutManager= LinearLayoutManager(context)
            appointment_container.adapter = appointmentAdapter
        }



    }

    init {
        servicesAdapter = ServiceAdapter(object : ServiceAdapter.OnServiceItemClickListener{
            override fun onItemClicked(servicesPOJO: ServicesPOJO) {
                val action = TodayFragmentDirections.todayToAddService(servicesPOJO)
                findNavController().navigate(action)
            }
        })

        appointmentAdapter = AppointmentAdapter(object :AppointmentAdapter.AppointmentListCallback{
            override fun onAppointmentDetailsClicked(appointment: AppointmentPOJO) {
                val bundle = Bundle()
                bundle.putParcelable("appointment",appointment)
                bundle.putString("appointment_id",null)
                findNavController().navigate(R.id.today_to_appointment_activity,bundle)
            }
        })
    }
}