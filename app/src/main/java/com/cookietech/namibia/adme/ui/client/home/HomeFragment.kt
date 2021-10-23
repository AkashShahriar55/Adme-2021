package com.cookietech.namibia.adme.ui.client.home

import android.app.Service
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.cookietech.namibia.adme.architecture.client.home.ClientHomeViewModel
import com.cookietech.namibia.adme.architecture.client.home.NearbyServiceCallback
import com.cookietech.namibia.adme.extensions.openNetworkSetting
import com.cookietech.namibia.adme.managers.ConnectionManager
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServiceCategory
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.bottom_details
import kotlinx.android.synthetic.main.fragment_home.bottom_details_back
import kotlinx.android.synthetic.main.fragment_home.client_notification_btn
import kotlinx.android.synthetic.main.fragment_home.map
import kotlinx.android.synthetic.main.fragment_home.today_notification_badge
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.networ_error.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var isBottomSheetVisible: Boolean = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var categoryIds: List<String?> = ArrayList()
    private var categories: ArrayList<ServiceCategory> = ArrayList()
    private var isMarkerSet: Boolean = false
    private var mMap: GoogleMap? = null
    private var availableServiceAdapter: AvailableServiceAdapter? = null
    val viewModel: ClientHomeViewModel by activityViewModels()
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val markers = arrayListOf<Marker?>()
    private val markerMaps = hashMapOf<Marker?,String?>()


    private var userRefToServicesMap : Map<String?,List<ServicesPOJO>>? = null



    val categoryObserver = Observer<ArrayList<ServiceCategory>>{categories->
        this.categories = categories
        availableServiceAdapter?.apply {
            Log.d("category_debug", "nearbyServicesObserver: $categoryIds")
            this.categories = categories.filter { it.id in categoryIds } as ArrayList<ServiceCategory>
            Log.d("category_debug", "categoryObserver: " + this.categories.size)
        }
    }

    val nearbyServicesObserver = Observer<ArrayList<ServicesPOJO>> { services ->

        if(!services.isNullOrEmpty()){
            Log.d("map_service", "initializeObservers: service asche " + services.size)

            userRefToServicesMap  = services.groupBy { it.user_ref }

            categoryIds = services.map { it.categoryId }.distinctBy { it }
            Log.d("category_debug", "nearbyServicesObserver: $categoryIds")


            availableServiceAdapter?.apply {
                this.categories = this@HomeFragment.categories.filter { it.id in categoryIds } as ArrayList<ServiceCategory>
                Log.d("category_debug", "nearbyServicesObserver: " + this.categories.size)
            }


            if (!isMarkerSet)
                updateMapMarkers(userRefToServicesMap!!)
            map_loader.visibility = View.GONE

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            isBottomSheetVisible= true
        }else{

            map_loader.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        setUpMap()

        FirebaseManager.currentUser?.let { user->
            val lat = user.lattitude?.toDoubleOrNull()
            val lng = user.longitude?.toDoubleOrNull()
            lat?.let { latitude-> lng?.let { longitude ->
                viewModel.fetchNearbyServices(latitude,longitude)
            } }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(userRefToServicesMap == null){
            map_loader.visibility = View.VISIBLE
        }
        initializeServicesRecyclerView()
        initializeClicksAndViews()
        ConnectionManager.networkAvailability.observe(viewLifecycleOwner,{
            if(it == false){
                showNetworkErrorMessage()
            }else{
                hideNetworkErrorMessage()
            }
        })
    }

    private fun initializeClicksAndViews() {

        if(FirebaseManager.currentUser?.hasUnreadNotifClient == true){
            today_notification_badge.visibility = View.VISIBLE
        }
        cv_search.setOnClickListener {
            findNavController().navigate(R.id.home_to_search_service)
        }

        client_location_button.setOnClickListener {
            FirebaseManager.currentUser?.let { user->
                map?.apply {
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


        bottom_details_back.setOnClickListener {
            findNavController().navigate(R.id.home_to_bottom_details)
        }

        client_notification_btn.setOnClickListener{
            findNavController().navigate(R.id.home_to_notification)
        }


        bottomSheetBehavior = BottomSheetBehavior.from(bottom_details)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_HIDDEN->{
                        bottomSheetBehavior.state= BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
    }

    private fun showNetworkErrorMessage() {
        network_error_holder_home.visibility = View.VISIBLE
        network_error_holder_home.tv_network_setting.setOnClickListener {
            requireContext().openNetworkSetting()
        }
    }

    private fun hideNetworkErrorMessage() {
        network_error_holder_home.visibility = View.GONE
    }

    private fun initializeObservers() {
        viewModel.categories.observe(viewLifecycleOwner, categoryObserver)
        viewModel.services.observe(viewLifecycleOwner,nearbyServicesObserver)
    }

    private fun removeObservers(){
        viewModel.categories.removeObserver(categoryObserver);
        viewModel.services.removeObserver(nearbyServicesObserver);
    }

    private fun updateMapMarkers(services:Map<String?,List<ServicesPOJO>>) {
        isMarkerSet = true
        markers.clear()
        markerMaps.keys.forEach { marker->
            marker?.remove()
        }
        for (service in services) {
            val singleService = service.value[0];
            val latitude= singleService.latitude?.toDouble()
            val longitude = singleService.longitude?.toDouble()
            val position = latitude?.let { lat->
                longitude?.let { lng ->
                    LatLng(
                        lat, lng
                    )
                }
            }

           Glide.with(requireContext())
               .asBitmap()
               .load(singleService.pic_url)
               .addListener(object : RequestListener<Bitmap> {
                   override fun onLoadFailed(
                       e: GlideException?,
                       model: Any?,
                       target: Target<Bitmap>?,
                       isFirstResource: Boolean
                   ): Boolean {
                       Log.d("bitmap_debug", "onLoadFailed: ${singleService.pic_url}")
                       val markerBitmap = viewModel.generateMarkerBitmap(requireContext(),BitmapFactory.decodeResource(
                           resources, R.drawable.profile
                       ))
                       addMarker(markerBitmap,position,service.key)
                       return false
                   }

                   override fun onResourceReady(
                       resource: Bitmap?,
                       model: Any?,
                       target: Target<Bitmap>?,
                       dataSource: DataSource?,
                       isFirstResource: Boolean
                   ): Boolean {
                       Log.d("bitmap_debug", "onResourceReady: ${singleService.pic_url}")
                       val markerBitmap = if (resource != null) {
                           viewModel.generateMarkerBitmap(requireContext(), resource)
                       } else {
                           viewModel.generateMarkerBitmap(requireContext(), BitmapFactory.decodeResource(
                               resources, R.drawable.profile
                           ))

                       }
                        addMarker(markerBitmap,position,service.key)

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

    private fun setUpMap() {
        Log.d("marker_debug", "setUpMap: " + isMarkerSet)
        isMarkerSet = false
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@HomeFragment)
        }

    }



    private fun initializeServicesRecyclerView() {
        available_service_rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        availableServiceAdapter = AvailableServiceAdapter(context,false,object :AvailableServiceAdapter.ItemClickListener{
            override fun onCategorySelected(category: ServiceCategory?) {

                viewModel.services.value?.apply {
                    if(category == null){
                        userRefToServicesMap  = this.groupBy { it.user_ref }
                        updateMapMarkers(userRefToServicesMap!!)
                    }else{
                        userRefToServicesMap  = this.filter { it.categoryId == category.id }.groupBy { it.user_ref }
                        updateMapMarkers(userRefToServicesMap!!)
                    }
                }
            }

        })
        viewModel.categories.value?.apply {
            Log.d("category_debug", "initializeServicesRecyclerView: $categoryIds")
            availableServiceAdapter?.categories = this
        }
        available_service_rv.adapter = availableServiceAdapter
      
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onMapReady(map: GoogleMap?) {
        Log.d("map_debug", "onMapReady: ")
        this.mMap = map
        mMap?.setOnMarkerClickListener(this)
        FirebaseManager.currentUser?.let { user->
            map?.apply {
                val lat = user.lattitude?.toDoubleOrNull()
                val lng = user.longitude?.toDoubleOrNull()

                val currentLocation = lat?.let { latitude-> lng?.let { longitude ->
                    LatLng(
                    latitude,
                    longitude
                ) } }
                clear()
                addMarker(currentLocation?.let { latlng ->
                    MarkerOptions().position(latlng)
                        .title("your location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker))
                })
                moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        }



        userRefToServicesMap?.apply {
            Log.d("marker_debug", "initializeObservers: service asche $size $isMarkerSet")
            if(!isMarkerSet)
                updateMapMarkers(this)
        }

    }


    fun addMarker(markerBitmap:Bitmap?,position:LatLng?,id:String?){
        val marker = mMap?.addMarker(
            MarkerOptions().position(position!!).icon(
                BitmapDescriptorFactory.fromBitmap(
                    markerBitmap
                )
            )
        )
        markers.add(marker)
        markerMaps[marker] = id
    }


    override fun onStart() {
        super.onStart()
        initializeObservers()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        Log.d("akash_debug", "onMarkerClick: ")
        marker?.apply {
            if(markerMaps.containsKey(this)){
                val userRef = markerMaps[this]
                val services = userRefToServicesMap!![userRef]
                val bundle = Bundle()
                Log.d("akash_debug", "onMarkerClick: " + services?.size)
                bundle.putParcelableArrayList("data", ArrayList(services!!))
                bundle.putString("user_ref",userRef)
                findNavController().navigate(R.id.home_to_marker_details,bundle)
            }
        }

        return true
    }


}