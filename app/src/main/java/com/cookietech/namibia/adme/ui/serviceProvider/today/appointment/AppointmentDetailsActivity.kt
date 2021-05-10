package com.cookietech.namibia.adme.ui.serviceProvider.today.appointment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.appointment_details_bottom_sheet.*
import kotlinx.android.synthetic.main.client_appointment_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_custom_toast_error.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class AppointmentDetailsActivity : AppCompatActivity(){
    val viewmodel: AppointmentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)
        val appointmentPOJO : AppointmentPOJO? = intent.extras?.getParcelable<AppointmentPOJO>("appointment")
        val appointmentId: String? = intent.extras?.getString("appointment_id")
        if (appointmentPOJO !=null){
            Log.d("notif_debug", "onCreate: not null appointment pojo")
            viewmodel.observableAppointment.value = appointmentPOJO
        } else if (appointmentId != null){
            Log.d("notif_debug", "onCreate: not null appointment id")
            viewmodel.fetchAppointment(appointmentId)
        }



    }


}