package com.cookietech.namibia.adme.ui.client.home

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.client.home.ServiceProviderDetailsViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.utils.GoogleMapUtils
import com.cookietech.namibia.adme.views.LoadingDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_service_provider_details.*
import kotlinx.android.synthetic.main.client_appointment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ServiceProviderDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var dialog: LoadingDialog
    private var phoneNumber: String? = null
    private var startTimeCalender: Calendar? = null
    private var endTimeCalender: Calendar? = null
    private lateinit var myCalendar: Calendar
    var service: ServicesPOJO? = null
    var feature_image_adapter: ViewServiceImageSliderAdapter? = null
    private var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val viewmodel:ServiceProviderDetailsViewModel by viewModels()
    var selectServiceAdapter:SelectServiceAdapter? = null
    val selectedServices = arrayListOf<SubServicesPOJO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider_details)

        service = intent.extras?.getParcelable("service")
        Log.d("service_debug", "onCreate: ${service?.user_name}")
        Log.d("service_debug", "onCreate: ${service?.category}")
        Log.d("service_debug", "onCreate: ${service?.feature_images?.size}")
        Log.d("service_debug", "onCreate: ${service?.latitude}")
        Log.d("service_debug", "onCreate: ${service?.user_ref}")
        initializeViews()


    }

    private fun initializeViews() {
        setUpQuotationBottomSheet()
        setUpServiceProviderInfo()
        setUpSubServicesAdapter()
        setUpMap()
        setUpObserver()
        setUpUserAddress()
        dialog = LoadingDialog(this, "Updating", "Please wait...")
        send_button.setOnClickListener {
            validateDataAndSendRequest()
        }

    }

    private fun validateDataAndSendRequest() {
        try {
            val client_name = FirebaseManager.currentUser?.user_name!!
            val client_phone = FirebaseManager.currentUser?.phone!!
            val client_ref = FirebaseManager.currentUser?.user_id!!
            val client_quotation = tv_service_quotation.text.toString()
            if(client_quotation.isEmpty()){
                tv_service_quotation.error = "Please fill up this"
                tv_service_quotation.requestFocus()
                return
            }
            val client_price = tv_service_money.text.toString()
            if(client_price.isEmpty()){
                tv_service_money.error = "Please fill up this"
                tv_service_money.requestFocus()
                return
            }
            val client_latitude = FirebaseManager.currentUser?.lattitude!!
            val client_longitude = FirebaseManager.currentUser?.longitude!!
            val client_address = edt_address.text.toString()
            if(client_address.isEmpty()){
                edt_address.error = "Please fill up this"
                edt_address.requestFocus()
                return
            }
            val client_time = myCalendar.timeInMillis.toString()
            if(tv_service_time.text.toString().isEmpty()){
                tv_service_time.error = "Please fill up this"
                tv_service_time.requestFocus()
                return
            }
            if(tv_service_date.text.toString().isEmpty()){
                tv_service_date.error = "Please fill up this"
                tv_service_date.requestFocus()
                return
            }
            val service_provider_name = service?.user_name!!
            val service_provider_phone = phoneNumber!!
            val service_provider_ref = service?.user_ref!!
            val service_name = service?.category!!
            val service_ref = service?.mServiceId!!
            val service_provider_latitude = service?.latitude!!
            val services_provider_longitude = service?.longitude!!
            val is_approved = false
            val state = "client_request_sent"
            val client_profile_pic = FirebaseManager.currentUser?.profile_image_url
            val service_provider_pic = service?.pic_url
            val time_in_millis = System.currentTimeMillis().toString()

            val appointment = AppointmentPOJO(client_name,client_phone,client_ref,client_quotation,client_price,client_latitude,client_longitude,client_address,client_time,service_provider_name,service_provider_phone,service_provider_ref,service_name,service_ref,null,null,service_provider_latitude,services_provider_longitude,null,is_approved,state,client_profile_pic, service_provider_pic,time_in_millis)
            showDialog("Sending Quotation","Please wait...")
            viewmodel.sendRequest(appointment,selectedServices,object : ServiceProviderDetailsViewModel.SendRequestCallback{
                override fun onRequestSentSuccessfully() {
                    hideDialog()
                    finish()
                }

                override fun onRequestSendFailed(exception: Exception) {
                    hideDialog()
                    Toast.makeText(this@ServiceProviderDetailsActivity,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                }

            })
        }catch (exception:Exception){
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }

    fun showDialog(title:String,message:String){
        if(!dialog.isShowing)
            dialog.show()
        dialog.updateTitle(title)
        dialog.updateMessage(message)
    }

    fun hideDialog(){
        if(dialog.isShowing)
            dialog.dismiss()
    }

    private fun setUpUserAddress() {

        FirebaseManager.currentUser?.apply {
            val lat = lattitude?.toDoubleOrNull() ?: 0.0
            val lng = longitude?.toDoubleOrNull() ?: 0.0
            val addresses: List<Address>
            val geocoder: Geocoder = Geocoder(
                this@ServiceProviderDetailsActivity,
                Locale.getDefault()
            )

            try {
                addresses = geocoder.getFromLocation(
                    lat,
                    lng,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                val address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName =
                    addresses[0].featureName // Only if available else return NULL'
                edt_address.setText(address)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }

    private fun setUpQuotationBottomSheet() {
        myCalendar = Calendar.getInstance()
        val date1 =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tv_service_date.setText(sdf.format(myCalendar.getTime()))
            }

        tv_service_time.setOnClickListener {
            val hour: Int = myCalendar.get(Calendar.HOUR_OF_DAY)
            val minute: Int = myCalendar.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                this@ServiceProviderDetailsActivity,
                { _, selectedHour, selectedMinute ->

                    val dummyCalender = Calendar.getInstance()
                    val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    dummyCalender.time = sdf.parse("$selectedHour:$selectedMinute")

                    if (dummyCalender < startTimeCalender || dummyCalender > endTimeCalender) {
                        Toast.makeText(
                            this,
                            "Service provider is not available",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }

                    myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    myCalendar.set(Calendar.MINUTE, selectedMinute)


                    val sdfs = SimpleDateFormat("hh:mm aa", Locale.getDefault())
                    tv_service_time.setText(sdfs.format(myCalendar.time))
                    //                        SimpleDateFormat sd = new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa", Locale.getDefault());
                    //                        Log.d(TAG, myCalendar.getTimeInMillis()+" initializeFields: "+sd.format(myCalendar.getTime()));
                    //                        Log.d(TAG, myCalendar.getTimeInMillis()+" getTimeInMillis "+ CookieTechUtilityClass.getTimeDifference(String.valueOf(myCalendar.getTimeInMillis()),"1592647202542"));
                }, hour, minute, false
            )
            mTimePicker.show()
        }

//        tv_service_date.setClickable(true);
//        tv_service_date.setFocusable(false);
//        tv_service_date.setInputType(InputType.TYPE_NULL);

//        tv_service_date.setClickable(true);
//        tv_service_date.setFocusable(false);
//        tv_service_date.setInputType(InputType.TYPE_NULL);
        tv_service_date.setOnClickListener {
            val mDatePickerDialog1 = DatePickerDialog(
                this@ServiceProviderDetailsActivity, date1,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            mDatePickerDialog1.datePicker.minDate = System.currentTimeMillis()
            mDatePickerDialog1.show()
        }
    }

    private fun setUpSubServicesAdapter() {
        selectServiceAdapter = SelectServiceAdapter(this,
            object : SelectServiceAdapter.SelectServiceAdapterListener {
                override fun onSelectServiceSelected(selectServiceItem: SubServicesPOJO) {
                    if(selectedServices.contains(selectServiceItem)){
                        if(selectServiceItem.quantity == 0){
                            selectedServices.remove(selectServiceItem)
                            Toast.makeText(
                                applicationContext,
                                selectServiceItem.service_name + " Removed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            selectedServices[selectedServices.indexOf(selectServiceItem)] = selectServiceItem
                        }

                    }else{
                        selectedServices.add(selectServiceItem)
                        Toast.makeText(
                            applicationContext,
                            selectServiceItem.service_name + " Added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setServiceOnBottomSheet()

                }

            })

        ad_service_recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        ad_service_recyclerView.adapter = selectServiceAdapter
    }

    private fun setUpObserver() {
        service?.user_ref?.let { userID-> service?.mServiceId?.let { serviceID ->
            viewmodel.fetchSubServices(userID, serviceID)
            viewmodel.fetchFullServiceDetails(userID, serviceID)
            viewmodel.fetchPhoneNumber(userID)
        } }
        viewmodel.observableSubServices.observe(this, { subServices ->
            subServices?.let { services ->
                selectServiceAdapter?.let { adapter ->
                    adapter.SelectServiceList = services
                }
            }
        })

        viewmodel.observableServiceFullDetails.observe(this, { service_pojo ->
            service = service_pojo
            setUpImageSlider()
            setUpWorkingTime()
        })

        viewmodel.observableServiceProviderUserInfo.observe(this,{
            phoneNumber = it?.phone
        })


    }

    private fun setUpWorkingTime() {
        val workingHour = service?.startTime + " To " + service?.endTime
        tv_working_hour.text = workingHour

        startTimeCalender = Calendar.getInstance()
        endTimeCalender = Calendar.getInstance()

        val sdf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
        startTimeCalender?.time = sdf.parse(service?.startTime ?: "10:00 AM") // all done
        endTimeCalender?.time = sdf.parse(service?.endTime ?: "8:00 PM") // all done
    }


    //    private void initia() {
    //        SelectServiceItem selectServiceItem1=new SelectServiceItem("Pattern Paint","It is call pattern paint.","90");
    //        SelectServiceItem selectServiceItem2=new SelectServiceItem("Rubber Paint","It is call Rubber paint.","70");
    //        SelectServiceItem selectServiceItem3=new SelectServiceItem("Artist Paint","It is call Artist paint.","110");
    //        selectServiceList.add(selectServiceItem1);
    //        selectServiceList.add(selectServiceItem2);
    //        selectServiceList.add(selectServiceItem3);
    //        runOnUiThread(new Runnable(){
    //            public void run() {
    //                service_adapter.notifyDataSetChanged();
    //            }
    //        });
    //    }


    fun setServiceOnBottomSheet() {
        var selected_service_text = "No Service Added"
        val selected_service_count = "${selectedServices.size} Service"
        var sum = 0f
        for (service in selectedServices.withIndex()){
            if(service.index == 0){
                selected_service_text = "1. "+ service.value.service_name + " ($" + service.value.service_charge + " x "+service.value.quantity +" "+service.value.service_unit+")"
            }else{
                selected_service_text += "\n" + (service.index + 1).toString() + ". "+ service.value.service_name + " ($" + service.value.service_charge + " x "+service.value.quantity+" "+service.value.service_unit+ ")"
            }

            sum += (service.value.service_charge?.toFloat() ?: 0f)*service.value.quantity
        }

        tv_service_added.setText(selected_service_text)
        tv_service_money.setText("$sum")
        tv_item_count.text = selected_service_count
        tv_total_price.text = "$ $sum"
    }

    private fun setUpMap() {
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@ServiceProviderDetailsActivity)
        }

    }


    private fun setUpServiceProviderInfo() {
        tv_username.text = service?.user_name
        tv_catagory.text = service?.category




        Glide.with(this)
            .load(service?.pic_url)
            .placeholder(R.mipmap.default_user_photo)
            .into(circleImageView)
    }

    private fun setUpImageSlider() {

        //feature image adapter setting
        Log.d("service_debug", "setUpImageSlider: " + service?.feature_images)

        imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        imageSlider.indicatorSelectedColor = Color.WHITE
        imageSlider.indicatorUnselectedColor = Color.GRAY
        feature_image_adapter = service?.let { ViewServiceImageSliderAdapter(
            this,
            it.feature_images
        ) }
        feature_image_adapter?.let { imageSlider.setSliderAdapter(it) }
        //select_service_recyclerView.setAdapter(service_adapter)
        //review_recyclerView.adapter = reviewAdapter
    }

    override fun onMapReady(map: GoogleMap?) {
        Log.d("map_debug", "onMapReady: ")
        map?.uiSettings?.isScrollGesturesEnabled = false;
        service?.let { servicePojo->
            map?.apply {
                val lat = servicePojo.latitude?.toDoubleOrNull()
                val lng = servicePojo.longitude?.toDoubleOrNull()
                Log.d("map_debug", "onMapReady: ")
                val currentLocation:LatLng = lat?.let { latitude-> lng?.let { longitude -> LatLng(
                    latitude,
                    longitude
                ) } } ?: LatLng(0.0, 0.0)
                Log.d("map_debug", "onMapReady: " + currentLocation)
                clear()
                addMarker(currentLocation?.let { latlng ->
                    MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker))
                })
                moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))


                val addresses: List<Address>
                val geocoder: Geocoder = Geocoder(
                    this@ServiceProviderDetailsActivity,
                    Locale.getDefault()
                )

                try {
                    addresses = geocoder.getFromLocation(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    val address =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    val city = addresses[0].locality
                    val state = addresses[0].adminArea
                    val country = addresses[0].countryName
                    val postalCode = addresses[0].postalCode
                    val knownName =
                        addresses[0].featureName // Only if available else return NULL'
                    tv_location_short.text = "$city,$state"
                    tv_location_details.text = address

                    FirebaseManager.currentUser?.apply {
                        val lat1 = lattitude?.toDoubleOrNull()
                        val lng1 = longitude?.toDoubleOrNull()
                        if(lat1 !=null && lng1 != null && lat != null && lng != null){
                            val distance = GoogleMapUtils.getDistanceInMiles(lat1, lng1, lat, lng)
                            tv_distance.text =  "%.2f".format(distance)
                        }


                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }




        }
    }
}