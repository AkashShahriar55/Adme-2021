package com.cookietech.namibia.adme.ui.serviceProvider.today.appointment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.appointment.AppointmentViewModel
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.utils.GoogleMapUtils
import com.cookietech.namibia.adme.utils.UiHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.appointment_details_bottom_sheet.*
import kotlinx.android.synthetic.main.appointment_details_bottom_sheet.tv_service_time
import kotlinx.android.synthetic.main.client_appointment_bottom_sheet.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    val viewmodel: AppointmentViewModel by viewModels()
    private lateinit var subServicesAdapter: AppointmentServicesAdapter
    private var mMap: GoogleMap? = null
    private var appointment: AppointmentPOJO? = null
    private var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        appointment = intent.extras?.getParcelable("appointment")
        setUpMap()
        updateAppointmentInfo()
        initializeObserver();


    }

    private fun initializeObserver() {
        viewmodel.observableServices.observe(this,
            { value ->
                value?.let {
                    subServicesAdapter.subServices = it
                }
            })
        viewmodel.fetchAppointmentServices(appointment?.id!!)
    }

    private fun updateAppointmentInfo() {
        tv_clint_time.text = UiHelper.getDate(appointment?.client_time?.toLong() ?: 0,"hh:mm aa, dd MMM yyyy")

        tv_clint_money.text = "$ ${appointment?.client_price}"
        tv_clint_name.text = appointment?.client_name
        tv_clint_address.text = appointment?.client_address
        tv_clint_text.text = appointment?.client_quotation
        tv_money.text = "Requested Money: $${appointment?.client_price}"


        subServicesAdapter = AppointmentServicesAdapter()
        appointment_services_rv.layoutManager = LinearLayoutManager(this)
        appointment_services_rv.adapter = subServicesAdapter


    }


    private fun setUpMap() {
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@AppointmentDetailsActivity)
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        Log.d("appointment_debug", "onMapReady: " + mMap + " "+ appointment)
        appointment?.let {
            updateMap()
        }
    }

    private fun updateMap() {
        appointment?.apply {
            mMap?.clear()
            val clientLat = client_latitude.toDouble()
            val clientLong = client_longitude.toDouble()
            val serviceProviderLat = service_provider_latitude.toDouble()
            val serviceProviderLng = service_provider_longitude.toDouble()

            val clientLoc = LatLng(clientLat, clientLong)
            val serviceProviderLoc = LatLng(serviceProviderLat, serviceProviderLng)

            if(SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER){
                mMap?.addMarker(
                    MarkerOptions().position(serviceProviderLoc)
                        .title("Your Current Location").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getIcon(
                                    R.drawable.service_provider,
                                    90,
                                    78
                                )
                            )
                        )
                )
                mMap?.addMarker(
                    MarkerOptions().position(clientLoc)
                        .title("Client Location")
                        .icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.client, 90, 78)))
                )
                val zoomBound = LatLngBounds.builder().include(clientLoc)
                    .include(serviceProviderLoc).build()
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 1000))
                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 100))
            }else{
                mMap?.addMarker(
                    MarkerOptions().position(serviceProviderLoc)
                        .title("Service Provider Location").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getIcon(
                                    R.drawable.service_provider,
                                    90,
                                    78
                                )
                            )
                        )
                )
                mMap?.addMarker(
                    MarkerOptions().position(clientLoc)
                        .title("Your Current Location")
                        .icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.client, 90, 78)))
                )
                val zoomBound = LatLngBounds.builder().include(clientLoc)
                    .include(serviceProviderLoc).build()
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 1000))
                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 100))
            }


            GoogleMapUtils.populateRoute(clientLoc,serviceProviderLoc,mMap)
            val distance = GoogleMapUtils.getDistanceInMiles(clientLat,clientLong,serviceProviderLat,serviceProviderLng)
            tv_distance.text = "%.2f".format(distance)



        }

    }

    fun getIcon(name: Int, height: Int, width: Int): Bitmap? {
        val bitmapdraw = resources.getDrawable(name) as BitmapDrawable
        val bitmap = bitmapdraw.bitmap
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}