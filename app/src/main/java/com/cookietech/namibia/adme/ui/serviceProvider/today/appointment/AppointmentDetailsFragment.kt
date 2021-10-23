package com.cookietech.namibia.adme.ui.serviceProvider.today.appointment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.Application.Status
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.appointment.AppointmentViewModel
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ReviewPOJO
import com.cookietech.namibia.adme.ui.invoice.RatingDialog

import com.cookietech.namibia.adme.utils.GoogleMapUtils
import com.cookietech.namibia.adme.utils.UiHelper
import com.cookietech.namibia.adme.views.LoadingDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.appointment_details_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_appointment_details.*
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

    private lateinit var dialog: LoadingDialog
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
        Log.d("notif_debug", "onViewCreated: ")
        dialog = context?.let { LoadingDialog(it, "Processing", "Please wait...") }!!
        initializeObserver()
        fab_back.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    private fun initializeObserver() {
        viewmodel.observableAppointment.observe(viewLifecycleOwner) {
            appointment = it
            Log.d("notif_debug", "observer: " + appointment?.id)
            viewmodel.fetchAppointmentServices(appointment?.id!!)
            setUpMap()
            updateAppointmentInfo()
        }

        viewmodel.observableServices.observe(viewLifecycleOwner
        ) { value ->
            value?.let {

                subServicesAdapter.subServices = it
            }
        }


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


        if(appointment?.approved == true){
            if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                fab_call.visibility = View.VISIBLE
                fab_call.setOnClickListener {
                    val phone = appointment?.client_phone
                    if (phone == "") {
                        Toast.makeText(
                            context,
                            "Phone number not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                        startActivity(intent)
                    }
                }
            } else {
                fab_call.visibility = View.VISIBLE
                fab_call.setOnClickListener {
                    val phone = appointment?.service_provider_phone
                    if (phone == "") {
                        Toast.makeText(
                            context,
                            "Phone number not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                        startActivity(intent)
                    }
                }
            }
        }else{
            fab_call.visibility = View.GONE
        }




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
            Status.status_provider_response_decline -> {
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
            Status.status_client_completion_declined->{
                stateUiCompletedActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForProviderResponseApproveProvider()
                } else {
                    setUpUiForProviderResponseApproveClient()
                }
            }
            Status.status_provider_receipt_sent->{
                stateUiPaymentActive()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForProviderInvoiceSentProvider()
                } else {
                    setUpUiForProviderInvoiceSentClient()
                }
            }
            Status.status_payment_completed->{
                stateUiPaymentCompleted()
                if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                    setUpUiForPaymentCompletedProvider()
                } else {
                    setUpUiForPaymentCompletedClient()
                }
            }
            Status.status_client_request_cancel->{
                stateUiAllInactive()
                setUpUiForAppointmentCanceled(true)
            }
            Status.status_provider_request_cancel->{
                stateUiAllInactive()
                setUpUiForAppointmentCanceled(false)
            }
        }
    }

    private fun setUpUiForAppointmentCanceled(isClient: Boolean) {
        service_provider_response_holder.visibility = View.GONE
        if(!appointment?.service_provider_quotation.isNullOrEmpty()&& !appointment?.service_provider_price.isNullOrEmpty()){
            service_provider_response.visibility = View.VISIBLE
            tv_provider_text.text = appointment?.service_provider_quotation
            tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        }else{
            service_provider_response.visibility = View.GONE
        }
        fab_call.visibility = View.GONE

        message_holder.visibility = View.VISIBLE
        accept_decline_holder.visibility = View.INVISIBLE
        if(appointment?.approved == true){
            tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        }else{
            tv_clint_money.text = "$ ${appointment?.client_price}"
        }


        if(isClient)
            tv_status_message.text = "This appointment was canceled by ${appointment?.client_name}"
        else
            tv_status_message.text = "This appointment was canceled by ${appointment?.service_provider_name}"
        tv_status_message.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.red
        ))
    }

    private fun setUpUiForPaymentCompletedClient() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.GONE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.VISIBLE
        negative_button.visibility = View.GONE
        invoice_show_btn.visibility = View.GONE
        rating_container.visibility = View.GONE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        positive_button.text = "Receipt"


        positive_button.setOnClickListener {
            findNavController().navigate(R.id.appointment_to_invoice_show)
        }


        if(appointment?.reviewed == false){
            openRatingDialog()
        }else{
            setUpUiForReviewDone()
        }


    }

    private fun setUpUiForReviewDone() {

        appointment?.apply {
            viewmodel.fetchReviewData(service_provider_ref,service_ref,review_ref).addOnSuccessListener {
                if(it.exists()){
                    val review = it.toObject(ReviewPOJO::class.java)
                    rating_container.visibility = View.VISIBLE
                    rating_time.text = UiHelper.getDate(review?.review_time?.toLong() ?:0,"dd MMM yyyy, hh:mm aa")
                    rating_bar.rating = review?.rating ?: 0.0f
                    rating_details.text = review?.review
                }

            }.addOnFailureListener {
                
            }
        }

    }

    private fun openRatingDialog() {
        val ratingDialog = RatingDialog.newInstance(object :RatingDialog.ReviewCallback{
            override fun onReviewed(rating: Float, review: String) {
                appointment?.apply {
                    val review = ReviewPOJO(client_name,client_ref,service_provider_name,service_provider_ref,rating,review,invoice_link,service_provider_price.toString(),System.currentTimeMillis().toString(),id.toString(),service_ref)
                    viewmodel.reviewService(review).addOnSuccessListener {
                        review_ref = it.id
                        reviewed = true
                        viewmodel.setReviewLinkInAppointment(this).addOnFailureListener {

                        }.addOnSuccessListener {
                            Toast.makeText(requireContext(),"Successfully reviewed",Toast.LENGTH_SHORT).show()
                            setUpUiForReviewDone()
                        }
                    }.addOnFailureListener {

                    }
                }
            }

        })
        ratingDialog.show(childFragmentManager,"rating_dialog")
    }

    private fun setUpUiForPaymentCompletedProvider() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.GONE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.VISIBLE
        negative_button.visibility = View.GONE
        invoice_show_btn.visibility = View.GONE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        positive_button.text = "Receipt"


        positive_button.setOnClickListener {
            findNavController().navigate(R.id.appointment_to_invoice_show)
        }

        if(appointment?.reviewed == true){
            setUpUiForReviewDone()
        }
    }

    private fun setUpUiForProviderInvoiceSentClient() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.GONE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.VISIBLE
        negative_button.visibility = View.GONE
        invoice_show_btn.visibility = View.GONE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        positive_button.text = "Receipt"


        positive_button.setOnClickListener {
            findNavController().navigate(R.id.appointment_to_invoice_show)
        }
    }

    private fun setUpUiForProviderInvoiceSentProvider() {
        service_provider_response_holder.visibility = View.GONE
        service_provider_response.visibility = View.VISIBLE
        message_holder.visibility = View.GONE
        accept_decline_holder.visibility = View.VISIBLE
        positive_button.visibility = View.VISIBLE
        negative_button.visibility = View.VISIBLE
        invoice_show_btn.visibility = View.VISIBLE


        tv_provider_text.text = appointment?.service_provider_quotation
        tv_provider_price.text = "Needed Money: $${appointment?.service_provider_price}"
        tv_clint_money.text = "$ ${appointment?.service_provider_price}"
        positive_button.text = "Payment Received"
        negative_button.text = "Resend Receipt"


        positive_button.setOnClickListener {
            completePayment()
        }

        invoice_show_btn.setOnClickListener {
            findNavController().navigate(R.id.appointment_to_invoice_show)
        }

        negative_button.setOnClickListener {
            findNavController().navigate(R.id.appointment_to_create_invoice)
        }
    }

    private fun completePayment() {
        appointment?.apply {
            total_income = service_provider_price?.toFloat() ?: 0.0f
            completed = true
            state = Status.status_payment_completed
            viewmodel.completePayment(this).addOnSuccessListener {
                stateUiPaymentCompleted()
                setUpUiForPaymentCompletedProvider()
            }.addOnFailureListener {

            }
        }
    }

    private fun stateUiAllInactive() {
        iv_state_pending.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
        pending_confirme_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.border_ash
            )
        )
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.border_ash
            )
        )
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
        complete_payment_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.border_ash
            )
        )
        iv_state_payment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
    }


    private fun stateUiConfirmedActive() {
        iv_state_pending.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        pending_confirme_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_active,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.border_ash
            )
        )
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_inactive,
                null
            )
        )
        complete_payment_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.border_ash
            )
        )
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
        pending_confirme_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_active,
                null
            )
        )
        complete_payment_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.border_ash
            )
        )
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
        pending_confirme_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        complete_payment_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
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
        pending_confirme_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_confirmed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        confirm_complete_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_completed.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
        complete_payment_line.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sv_primary
            )
        )
        iv_state_payment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_state_completed,
                null
            )
        )
    }



    private fun sendWorkCompleted() {
        dialog.show()
        dialog.updateTitle("Processing")
        appointment?.apply {
            state = Status.status_provider_work_completed
            viewmodel.sendWorkCompleted(this).addOnSuccessListener {
                setUpUiForProviderWorkCompletedProvider()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
            }
        }
    }




    private fun approveServiceProviderResponse() {
        dialog.show()
        dialog.updateTitle("Approving")
        appointment?.apply {
            approved = true
            state = Status.status_provider_response_approve
            viewmodel.approveServiceProviderResponse(this).addOnSuccessListener {
                stateUiCompletedActive()
                setUpUiForProviderResponseApproveClient()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
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
            dialog.show()
            dialog.updateTitle("Sending")
            viewmodel.sendServiceProviderResponse(this).addOnSuccessListener {
                Toast.makeText(requireContext(), "Response sent", Toast.LENGTH_SHORT).show()
                setUpUiForProviderResponseSendProvider()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
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
            cancelAppointment(false)
        }
    }

    private fun cancelAppointment(isClient: Boolean) {
        AlertDialog.Builder(requireContext()).setTitle("Are you sure?")
            .setMessage("Do you want to cancel this appointment?. This can't be undone !")
            .setPositiveButton("No",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }

            })
            .setNegativeButton("Yes",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    handleCancelAppointment(isClient)
                }

            }).create().show()
    }

    private fun handleCancelAppointment(isClient: Boolean) {
        dialog.show()
        dialog.updateTitle("Canceling")

        appointment?.apply {
            state = if(isClient)
                Status.status_client_request_cancel
            else
                Status.status_provider_request_cancel
            viewmodel.cancelAppointmentFromClient(this).addOnSuccessListener {
                Log.d("cancel_debug", "handleCancelAppointment: ")
                requireActivity().onBackPressed()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
            }
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

        negative_button.setOnClickListener {
            cancelAppointment(true)
        }
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

        negative_button.setOnClickListener {
            declineServiceProviderResponse()
        }
    }

    private fun declineServiceProviderResponse() {
        dialog.show()
        dialog.updateTitle("Declining")
        appointment?.apply {
            approved = false
            state = Status.status_provider_response_decline
            viewmodel.declineServiceProviderResponse(this).addOnSuccessListener {
                setUpUiForClientRequestSentClient()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
            }
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

        fab_call.visibility = View.VISIBLE
        fab_call.setOnClickListener {
            val phone = appointment?.service_provider_phone
            if (phone == "") {
                Toast.makeText(
                    context,
                    "Phone number not found",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }
        }

        negative_button.setOnClickListener {
            cancelAppointment(true)
        }
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

        fab_call.visibility = View.VISIBLE
        fab_call.setOnClickListener {
            val phone = appointment?.client_phone
            if (phone == "") {
                Toast.makeText(
                    context,
                    "Phone number not found",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }
        }

        positive_button.setOnClickListener {
            sendWorkCompleted()
        }

        negative_button.setOnClickListener {
            cancelAppointment(false)
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

        negative_button.setOnClickListener {
            declineProviderWorkCompletion()
        }
    }

    private fun declineProviderWorkCompletion() {
        dialog.show()
        dialog.updateTitle("Declining")
        appointment?.apply {
            approved = false
            state = Status.status_client_completion_declined
            viewmodel.declineServiceProviderWorkCompletion(this).addOnSuccessListener {
                setUpUiForProviderResponseApproveClient()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
            }
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
        dialog.show()
        dialog.updateTitle("Approving")
        appointment?.apply {
            state = Status.status_client_completion_approve
            viewmodel.approveProviderWorkCompletion(this).addOnSuccessListener {
                stateUiPaymentActive()
                setUpUiForClientCompletionApprovedClient()
                dialog.hide()
            }.addOnFailureListener {
                dialog.hide()
            }
        }
    }




    private fun setUpMap() {
        mainScope.launch {
            Log.d("notif_debug", "setUpMap: ")
            val mf = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@AppointmentDetailsFragment)
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        Log.d("notif_debug", "onMapReady: " + mMap + " " + appointment)
        appointment?.let {
            mMap?.setOnMapLoadedCallback(OnMapLoadedCallback {
                updateMap()
            })

        }
    }

    private fun updateMap() {
        Log.d("notif_debug", "updateMap: ")
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