package com.cookietech.namibia.adme.ui.client.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.client.home.ClientHomeViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.ServicesPOJO
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

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var availableServiceAdapter: AvailableServiceAdapter? = null
    val viewModel: ClientHomeViewModel by activityViewModels()
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val markerOptions = arrayListOf<MarkerOptions>()
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
        initializeObservers();
        initializeServicesRecyclerView()
        setUpMap()
        initializeClicksAndViews()
    }

    private fun initializeClicksAndViews() {
        cv_search.setOnClickListener {
            findNavController().navigate(R.id.home_to_search_service)
        }
    }

    private fun initializeObservers() {
        viewModel.categories.observe(viewLifecycleOwner, Observer { service_list ->
            availableServiceAdapter?.apply {
                categories = service_list
            }
        })


        viewModel.services.observe(viewLifecycleOwner, Observer { services ->
            services?.apply {
                updateMapMarkers(this)
            }
        })


    }

    private fun updateMapMarkers(services: ArrayList<ServicesPOJO>) {
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
               .into(object : CustomTarget<Bitmap>() {
                   override fun onResourceReady(
                       resource: Bitmap,
                       transition: Transition<in Bitmap>?
                   ) {
                       val markerBitmap = viewModel.generateMarkerBitmap(requireContext(),resource)
                       val marker =  MarkerOptions().position(position!!).icon(
                           BitmapDescriptorFactory.fromBitmap(
                               markerBitmap
                           )
                       ).title(service.user_name)
                       markerOptions.add(marker)
                   }

                   override fun onLoadCleared(placeholder: Drawable?) {

                   }

               })


        }
    }

    private fun setUpMap() {
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
        availableServiceAdapter = AvailableServiceAdapter(context)
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


        for(marker in markerOptions){
            mMap?.addMarker(marker)
        }

    }
}