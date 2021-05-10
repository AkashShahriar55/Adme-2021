package com.cookietech.namibia.adme.ui.serviceProvider.today.appointment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.Application.Status
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.appointment.AppointmentViewModel
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.ui.invoice.CreateInvoice
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class AppointmentDetailsFragment : Fragment(), OnMapReadyCallback {


    val viewmodel: AppointmentViewModel by activityViewModels()
    private lateinit var subServicesAdapter: AppointmentServicesAdapter
    private var mMap: GoogleMap? = null
    private var appointment: AppointmentPOJO? = null
    private var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMap()
        initializeObserver()
    }

    private fun initializeObserver() {
        viewmodel.observableAppointment.observe(viewLifecycleOwner,{
            appointment = it
            Log.d("notif_debug", "observer: id" + appointment?.id)
            viewmodel.fetchAppointmentServices(appointment?.id!!)
            setUpMap()
            updateAppointmentInfo()
        })

        viewmodel.observableServices.observe(viewLifecycleOwner,
            { value ->
                value?.let {
                    subServicesAdapter.subServices = it
                }
            })

    }

    private fun updateAppointmentInfo() {
        tv_clint_time.text = UiHelper.getDate(
            appointment?.client_time?.toLong() ?: 0,
            "hh:mm aa, dd MMM yyyy"
        )
        tv_clint_money.text = "$ ${appointment?.client_price}"
        tv_clint_name.text = appointment?.client_name
        tv_clint_address.text = appointment?.client_address
        tv_clint_text.text = appointment?.client_quotation
        tv_money.text = "Requested Money: $${appointment?.client_price}"


        subServicesAdapter = AppointmentServicesAdapter()
        appointment_services_rv.layoutManager = LinearLayoutManager(requireContext())
        appointment_services_rv.adapter = subServicesAdapter


//        if(SharedPreferenceManager.user_mode == AppComponent.MODE_CLIENT){
//            service_provider_response_holder.visibility = View.GONE
//        }
//
//        provider_send_button.setOnClickListener {
//            sendProviderResponse()
//        }
//
//        edt_needed_money.setText(appointment?.client_price)


        updateUiForStatus(appointment!!.state)

    }

    private fun updateUiForStatus(state: String) {
        when(state){
            Status.status_client_request_sent -> {
                stateUiConfirmedActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForClientRequestSentProvider()
                } else {
                    setUpUiForClientRequestSentClient()
                }
            }
            Status.status_provider_response_sent -> {
                stateUiConfirmedActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForProviderResponseSendProvider()
                } else {
                    setUpUiForProviderResponseSendClient()
                }
            }
            Status.status_provider_response_approve -> {
                stateUiCompletedActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForProviderResponseApproveProvider()
                } else {
                    setUpUiForProviderResponseApproveClient()
                }
            }
            Status.status_provider_work_completed -> {
                stateUiCompletedActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForProviderWorkCompletedProvider()
                } else {
                    setUpUiForProviderWorkCompletedClient()
                }
            }
            Status.status_client_completion_approve -> {
                stateUiPaymentActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForClientCompletionApprovedProvider()
                } else {
                    setUpUiForClientCompletionApprovedClient()
                }
            }
        }
    }




    private fun stateUiConfirmedActive() {
        iv_state_pending.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        pending_confirme_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_active,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.border_ash))
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
        complete_payment_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.border_ash))
        iv_state_payment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
    }

    private fun stateUiCompletedActive() {
        iv_state_pending.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        pending_confirme_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_active,
                null
            )
        )
        complete_payment_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.border_ash))
        iv_state_payment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
    }

    private fun stateUiPaymentActive() {
        iv_state_pending.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        pending_confirme_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        complete_payment_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_payment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_active,
                null
            )
        )
    }

    private fun stateUiPaymentCompleted() {
        iv_state_pending.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        pending_confirme_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        complete_payment_line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sv_primary))
        iv_state_payment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
    }



    private fun sendWorkCompleted() {
        appointment?.apply {
            state = Status.status_provider_work_completed
            viewmodel.sendWorkCompleted(this).addOnSuccessListener {
                setUpUiForProviderWorkCompletedProvider()
            }.addOnFailureListener {

            }
        }
    }




    private fun approveServiceProviderResponse() {
        appointment?.apply {
            approved = true
            state = Status.status_provider_response_approve
            viewmodel.approveServiceProviderResponse(this).addOnSuccessListener {
                stateUiCompletedActive()
                setUpUiForProviderResponseApproveClient()
            }.addOnFailureListener {

            }
        }

    }




    private fun sendProviderResponse() {
        val neededMoney = edt_needed_money.text.toString()
        if(neededMoney.isEmpty()){
            edt_needed_money.error = "please fill this"
            edt_needed_money.requestFocus()
            return
        }

        val providerResponse = edt_provider_response.text.toString()
        if(providerResponse.isEmpty()){
            edt_provider_response.error = "please fill this"
            edt_provider_response.requestFocus()
            return
        }

        appointment?.apply {
            service_provider_price = neededMoney
            service_provider_quotation = providerResponse
            state = "provider_response_sent"
            viewmodel.sendServiceProviderResponse(this).addOnSuccessListener {
                Toast.makeText(requireContext(), "Response sent", Toast.LENGTH_SHORT).show()
                setUpUiForProviderResponseSendProvider()
            }.addOnFailureListener {

            }
        }

    }


    private fun setUpUiForClientRequestSentProvider() {
        accept_decline_holder.visibility = View.GONE
        message_holder.visibility = View.GONE
        service_provider_response_holder.visibility = View.VISIBLE
        service_provider_response.visibility = View.GONE

        edt_needed_money.setText(appointment?.client_price)

        provider_send_button.setOnClickListener {
            sendProviderResponse()
        }

        provider_cancel_button.setOnClickListener {

        }
    }

    private fun setUpUiForClientRequestSentClient() {
        accept_decline_holder.visibility = View.VISIBLE
        message_holder.visibility = View.VISIBLE
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.GONE

        tv_status_message.text = "Waiting for service provider response"

        positive_button.visibility = View.GONE
        negative_button.visibility = View.VISIBLE

        negative_button.text = "Cancel request"
    }

    private fun setUpUiForProviderResponseSendProvider() {
        service_provider_response.visibility = View.VISIBLE
        service_provider_response_holder.visibility = View.GONE
        message_holder.visibility = View.VISIBLE
        accept_decline_holder.visibility = View.GONE

        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"

        tv_status_message.text = "Waiting for client acceptance"
    }

    private fun setUpUiForProviderResponseSendClient() {
        service_provider_response_holder.visibility = View.GONE
        message_holder.visibility = View.GONE
        service_provider_response.visibility =  View.VISIBLE
        accept_decline_holder.visibility = View.VISIBLE

        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"

        positive_button.text = "Approve"
        negative_button.text = "Decline"

        positive_button.setOnClickListener {
            approveServiceProviderResponse()
        }
    }

    private fun setUpUiForProviderResponseApproveClient() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.VISIBLE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.GONE
        negative_button.visibility = View.VISIBLE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        tv_status_message.text = "You can cancel this request before time"
        negative_button.text = "Cancel"
    }

    private fun setUpUiForProviderResponseApproveProvider() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.GONE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.VISIBLE
        negative_button.visibility = View.VISIBLE


        positive_button.text = "Work completed"
        negative_button.text = "Cancel request"
        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"

        positive_button.setOnClickListener {
            sendWorkCompleted()
        }
    }

    private fun setUpUiForProviderWorkCompletedProvider() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.VISIBLE
        accept_decline_holder.visibility = View.GONE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        tv_status_message.text = "Waiting for client approval"

    }

    private fun setUpUiForProviderWorkCompletedClient() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.VISIBLE
        accept_decline_holder.visibility = View.VISIBLE

        positive_button.text = "Approve"
        negative_button.text = "Decline"
        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        tv_status_message.text = "Approve service provider work to proceed"

        positive_button.setOnClickListener {
            approveProviderWorkCompletion()
        }
    }

    private fun setUpUiForClientCompletionApprovedClient() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.VISIBLE
        accept_decline_holder.visibility = View.GONE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        tv_status_message.text = "Waiting for receipt"
    }

    private fun setUpUiForClientCompletionApprovedProvider() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.GONE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.VISIBLE
        negative_button.visibility = View.GONE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        positive_button.text = "Send receipt for payment"

        positive_button.setOnClickListener {
            findNavController().navigate(R.id.appointment_to_create_invoice)
        }

    }

    private fun approveProviderWorkCompletion() {
        appointment?.apply {
            state = Status.status_client_completion_approve
            viewmodel.approveProviderWorkCompletion(this).addOnSuccessListener {
                stateUiPaymentActive()
                setUpUiForClientCompletionApprovedClient()
            }.addOnFailureListener {

            }
        }
    }




    private fun setUpMap() {
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@AppointmentDetailsFragment)
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        Log.d("appointment_debug", "onMapReady: " + mMap + " " + appointment)
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
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getIcon(
                                    R.drawable.client,
                                    90,
                                    78
                                )
                            )
                        )
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
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getIcon(
                                    R.drawable.client,
                                    90,
                                    78
                                )
                            )
                        )
                )
                val zoomBound = LatLngBounds.builder().include(clientLoc)
                    .include(serviceProviderLoc).build()
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 1000))
                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 100))
            }


            GoogleMapUtils.populateRoute(clientLoc, serviceProviderLoc, mMap)
            val distance = GoogleMapUtils.getDistanceInMiles(
                clientLat,
                clientLong,
                serviceProviderLat,
                serviceProviderLng
            )
            tv_distance.text = "%.2f".format(distance)



        }

    }

    fun getIcon(name: Int, height: Int, width: Int): Bitmap? {
        val bitmapdraw = resources.getDrawable(name) as BitmapDrawable
        val bitmap = bitmapdraw.bitmap
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AppointmentDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}