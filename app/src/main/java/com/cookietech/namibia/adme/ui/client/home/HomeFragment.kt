package com.cookietech.namibia.adme.ui.client.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
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
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.ui.client.home.search.SearchData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var isMarkerSet: Boolean = false
    private var mMap: GoogleMap? = null
    private var availableServiceAdapter: AvailableServiceAdapter? = null
    val viewModel: ClientHomeViewModel by activityViewModels()
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val markers = arrayListOf<Marker?>()
    private val markerMaps = hashMapOf<Marker?,ServicesPOJO?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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
        initializeObservers()
        initializeServicesRecyclerView()
        setUpMap()
        initializeClicksAndViews()
    }

    private fun initializeClicksAndViews() {
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


        bottom_details_button.setOnClickListener {
            findNavController().navigate(R.id.home_to_bottom_details)
        }

        client_notification_btn.setOnClickListener{
            findNavController().navigate(R.id.home_to_notification)
        }
    }

    private fun initializeObservers() {
        viewModel.categories.observe(viewLifecycleOwner, { service_list ->
            availableServiceAdapter?.apply {
                categories = service_list
            }
        })





    }

    private fun updateMapMarkers(services: ArrayList<ServicesPOJO>) {
        isMarkerSet = true
        markers.clear()
        markerMaps.keys.forEach { marker->
            marker?.remove()
        }
        for (service in services) {
            val latitude= service.latitude?.toDouble()
            val longitude = service.longitude?.toDouble()
            val position = latitude?.let { lat->
                longitude?.let { lng ->
                    LatLng(
                        lat, lng
                    )
                }
            }

           Glide.with(requireContext())
               .asBitmap()
               .load(service.pic_url)
               .addListener(object : RequestListener<Bitmap> {
                   override fun onLoadFailed(
                       e: GlideException?,
                       model: Any?,
                       target: Target<Bitmap>?,
                       isFirstResource: Boolean
                   ): Boolean {
                       Log.d("bitmap_debug", "onLoadFailed: ${service.pic_url}")
                       val markerBitmap = viewModel.generateMarkerBitmap(requireContext(),BitmapFactory.decodeResource(
                           resources, R.drawable.profile
                       ))
                       addMarker(markerBitmap,position,service)
                       return false
                   }

                   override fun onResourceReady(
                       resource: Bitmap?,
                       model: Any?,
                       target: Target<Bitmap>?,
                       dataSource: DataSource?,
                       isFirstResource: Boolean
                   ): Boolean {
                       Log.d("bitmap_debug", "onResourceReady: ${service.pic_url}")
                       val markerBitmap = if (resource != null) {
                           viewModel.generateMarkerBitmap(requireContext(), resource)
                       } else {
                           viewModel.generateMarkerBitmap(requireContext(), BitmapFactory.decodeResource(
                               resources, R.drawable.profile
                           ))

                       }
                        addMarker(markerBitmap,position,service)

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
        availableServiceAdapter = AvailableServiceAdapter(context,false)
        viewModel.categories.value?.apply {
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
        this.mMap = map
        mMap?.setOnMarkerClickListener(this)
        FirebaseManager.currentUser?.let { user->
            map?.apply {
                val lat = user.lattitude?.toDoubleOrNull()
                val lng = user.longitude?.toDoubleOrNull()
                val currentLocation = lat?.let { latitude-> lng?.let { longitude -> LatLng(
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


        viewModel.services.observe(viewLifecycleOwner, { services ->
            services?.apply {
                Log.d("marker_debug", "initializeObservers: service asche " + services.size)
                if (!isMarkerSet)
                    updateMapMarkers(this)
            }
        })


        viewModel.services.value?.apply {
            Log.d("marker_debug", "initializeObservers: service asche $size $isMarkerSet")
            if(!isMarkerSet)
                updateMapMarkers(this)
        }

    }


    fun addMarker(markerBitmap:Bitmap?,position:LatLng?,id:ServicesPOJO?){
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

    override fun onMarkerClick(marker: Marker?): Boolean {
        Log.d("akash_debug", "onMarkerClick: ")
        marker?.apply {
            if(markerMaps.containsKey(this)){
                val service = markerMaps[this]
                val bundle = Bundle()
                bundle.putParcelable("data",service)
                findNavController().navigate(R.id.home_to_marker_details,bundle)
            }
        }

        return true
    }


}