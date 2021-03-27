package com.cookietech.namibia.adme.ui.loginRegistration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.BuildConfig
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.RegistrationViewModel
import com.cookietech.namibia.adme.architecture.loginRegistration.UserInfoViewModel
import com.cookietech.namibia.adme.interfaces.ImageUploadCallback
import com.cookietech.namibia.adme.interfaces.UpdateDataCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.PermissionManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.views.LoadingDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class UserInfoFragment : Fragment(), OnMapReadyCallback{
    private var lng: Double = 0.0
    private var lat: Double = 0.0
    private var imageDownloadUrl: String? = null
    private var isPhoneVerifyed: Boolean = false
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 11
    private lateinit var dialog: LoadingDialog
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val userInfoViewModel: UserInfoViewModel by viewModels()
    val registrationViewModel: RegistrationViewModel by viewModels()
    var mMap: GoogleMap? = null
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
         return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = context?.let { LoadingDialog(it, "Updating", "Please wait...") }!!
        ccp.registerCarrierNumberEditText(edt_phone_number)
        ccp.detectSIMCountry(true)
        ccp.setCustomMasterCountries("NA,BD")
        setUpInfo()
        setUpMap()
        initializeObserver()

        current_location_btn.setOnClickListener {
            Log.d("location_debug", "onViewCreated: ")
            PermissionManager.checkLocationPermission(
                requireContext(),
                object : PermissionManager.SimplePermissionCallback {
                    override fun onPermissionGranted() {
                        Log.d("permission_debug", "finally onPermissionGranted: ")
                        mainScope.launch {
                            fetchCurrentLocation()
                        }

                    }

                    override fun onPermissionDenied() {
                        Log.d("permission_debug", "finally onPermissionGranted: ")
                    }

                },
                main_content_view
            )
        }


        profile_photo.setOnClickListener {
            openPicker()
        }

        profile_continue_btn.setOnClickListener {
            verifyAndLogin()

        }
    }

    private fun verifyAndLogin() {
        if(edt_profile_username.editText?.text.isNullOrBlank()){
            edt_profile_username.editText?.error = "Fill this properly"
            edt_profile_username.editText?.findFocus()

            return
        }

        if(!ccp.isValidFullNumber){
            edt_phone_number.error = "Fill this properly"
            edt_phone_number.findFocus()
            return
        }

        if(isPhoneVerifyed){
            uploadImageToServer()
        }else{
            dialog.show()
            dialog.updateTitle("Linking phone no.")
            registrationViewModel.registrationCallbacks = object :RegistrationViewModel.RegistrationCallbacks{
                override fun onCodeSend() {
                    dialog.dismiss()
                    childFragmentManager.let {
                        OTPBottomSheetDialog.newInstance().apply {
                            callback = object : OTPBottomSheetDialog.OtpBottomSheetCallback{
                                override fun onCodeGiven(code: String) {
                                    if(code.isNotEmpty()){
                                        registrationViewModel.linkWithPhoneAuthCredentialForCode(
                                            code.toString()
                                        )
                                    }
                                }

                                override fun resendVerification() {
                                    verifyAndLogin()
                                }

                            }
                            isCancelable = false
                            show(it, tag)
                        }
                    }
                }

                override fun onLoginSuccessful() {
                    dialog.dismiss()
                    uploadImageToServer()
                }

                override fun onLoginFailed() {
                    dialog.dismiss()
                }

            }
            registrationViewModel.sendVerificationCode(
                ccp.fullNumberWithPlus,
                requireActivity(),
                true
            )
        }


    }

    private fun uploadImageToServer() {
        dialog.show()
        dialog.updateTitle("Updating data...")

        imageUri?.apply {
            userInfoViewModel.uploadImage(this,object : ImageUploadCallback{
                override fun onImageUploaded(url: String) {
                    Log.d("update_debug", "onImageUploaded ")
                    imageDownloadUrl = url
                    updateUserData()
                }

                override fun onImageUploadFailed() {
                    Log.d("update_debug", "onImageUploadFailed ")
                    dialog.dismiss()
                }

            })
        } ?: kotlin.run {
            Log.d("update_debug", "imageUri null: ")
            updateUserData()
        }




    }

    private fun updateUserData() {
        val name = edt_profile_username.editText?.text.toString()
        val phone_number = ccp.fullNumberWithPlus.toString()
        val address = edt_profile_address.editText?.text.toString()
        name?.let {name->
            phone_number?.let { phone_number->
                userInfoViewModel.updateUserInfo(name,phone_number,lat.toString(),lng.toString(),imageDownloadUrl,object : UpdateDataCallback{
                    override fun updateSuccessful() {
                        dialog.dismiss()
                        when(SharedPreferenceManager.user_mode){
                            AppComponent.MODE_CLIENT -> navigateToClientActivity()
                            AppComponent.MODE_SERVICE_PROVIDER -> navigateToServiceActivity()
                        }
                    }

                    override fun updateFailed() {
                        dialog.dismiss()
                    }

                })
            }
        }

    }

    private fun navigateToServiceActivity() {
        findNavController().navigate(R.id.user_info_to_service_activity)
    }

    private fun navigateToClientActivity() {
        findNavController().navigate(R.id.user_info_to_client_activity)
    }

    private fun openPicker() {
        Log.d("akash_debug", "openPicker: ")
        Dexter.withContext(context)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(
                        intent,
                        IMAGE_PICK_CODE
                    )
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (BuildConfig.DEBUG && data == null) {
                error("Assertion failed")
            }
            imageUri = data?.data
            //profile_photo.setImageURI(data.getData());
            Glide.with(this)
                .load(data?.data)
                .fitCenter()
                .into(profile_photo)
        }
    }

    private fun initializeObserver() {


    }

    private fun fetchCurrentLocation() {
            userInfoViewModel.fetchCurrentLocation(requireActivity()).observe(viewLifecycleOwner,
                {
                    it?.let { location ->
                        mMap?.let { map ->
                            lat = location.latitude
                            lng = location.longitude
                            val currentLocation = LatLng(location.latitude, location.longitude)
                            map.clear()
                            map.addMarker(
                                MarkerOptions().position(currentLocation)
                                    .title("your location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker))
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));

                            val addresses: List<Address>
                            val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

                            try {
                                addresses = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
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
                                edt_txt_address.setText(address)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                })

    }

    private fun setUpMap() {
        mainScope.launch {
            val mf = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.map, mf).commitAllowingStateLoss()
            mf.getMapAsync(this@UserInfoFragment)
        }


    }

    private fun setUpInfo() {
        Log.d("akash_debug", "setUpInfo: " + FirebaseManager.mFirebaseUser)
        if(FirebaseManager.mFirebaseUser != null){
            if(!FirebaseManager.mFirebaseUser?.phoneNumber.isNullOrEmpty()){
                Log.d("akash_phonenumber_debug", "setUpInfo:  ${FirebaseManager.mFirebaseUser?.phoneNumber}")
                ccp.fullNumber = FirebaseManager.mFirebaseUser?.phoneNumber
                ccp.isEnabled = false
                edt_phone_number.isEnabled = false
                isPhoneVerifyed = true
            }
            Glide.with(this).load(FirebaseManager.mFirebaseUser?.photoUrl).into(profile_photo)
            edt_profile_username.editText?.setText(FirebaseManager.mFirebaseUser?.displayName)
        }

        Log.d("location_debug", "onViewCreated: ")
        PermissionManager.checkLocationPermission(
            requireContext(),
            object : PermissionManager.SimplePermissionCallback {
                override fun onPermissionGranted() {
                    Log.d("permission_debug", "finally onPermissionGranted: ")
                    mainScope.launch {
                        fetchCurrentLocation()
                    }

                }

                override fun onPermissionDenied() {
                    Log.d("permission_debug", "finally onPermissionGranted: ")
                }

            },
            main_content_view
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhoneVerficationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.mMap = map
    }

}