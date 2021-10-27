package com.cookietech.namibia.adme.ui.common.profile

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.profile.ProfileViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.ServiceProviderViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.ui.client.ClientActivity
import com.cookietech.namibia.adme.ui.serviceProvider.ServiceProviderActivity
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    val profileViewModel : ProfileViewModel by viewModels()
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val serviceProviderViewModel: ServiceProviderViewModel by activityViewModels()

    init {

    }

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialFields()
        initializeClicks()
    }

    private fun initializeClicks() {
        cardChangeMode.setOnClickListener {
            changeMode()
        }

        cardLogout.setOnClickListener{
            logout()
        }

        cardContacts.setOnClickListener {
            findNavController().navigate(R.id.action_service_provider_profileFragment_to_contactInfoFragment)
        }
    }

    private fun logout() {

        profileViewModel.logout(requireContext())
        workerScope.launch {
            FirebaseMessaging.getInstance().deleteToken()
        }

    }

    private fun changeMode() {

        if(SharedPreferenceManager.user_mode == AppComponent.MODE_CLIENT){
            val intent = Intent(requireContext(), ServiceProviderActivity::class.java).apply {
                flags = FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            SharedPreferenceManager.user_mode = AppComponent.MODE_SERVICE_PROVIDER
        }else{
            val intent = Intent(requireContext(), ClientActivity::class.java).apply {
                flags = FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            SharedPreferenceManager.user_mode = AppComponent.MODE_CLIENT
        }

        requireActivity().finish()
    }

    private fun initialFields() {
        setProfilePhoto()
        setUserName()
        setMemberSince()
        setSwitchMode()
        setRatingAndServices()
    }

    private fun setRatingAndServices() {
        if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER){
            tv_sp_rating.text = serviceProviderViewModel.service_provider_data.value?.rating.toString()
            tv_sp_services.text = serviceProviderViewModel.service_provider_data.value?.completed.toString()
        }
    }

    private fun setSwitchMode() {
        if (SharedPreferenceManager.user_mode == AppComponent.MODE_CLIENT){
            tv_swith_mode.text = "Switch to Business Mode"
            service_provider_section.visibility = View.GONE
        } else{
            tv_swith_mode.text = "Switch to Client Mode"
            service_provider_section.visibility = View.VISIBLE
        }
    }

    private fun setMemberSince() {
        val simpleDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        FirebaseManager.currentUser?.registration_date?.let {
            val registration_date : String = simpleDate.format(it.toDate())

            //Log.d("setMemberSince", "setMemberSince: " + registraion_date)
            sinceTime.text = "Member Since $registration_date"
        }



    }

    private fun setProfilePhoto() {
        Log.d("pro_pic_debug", "setProfilePhoto: " + FirebaseManager.mFirebaseUser!!.photoUrl)
        FirebaseManager.currentUser?.profile_image_url?.let {
            Glide.with(requireContext())
                .load(it)
                .placeholder(R.mipmap.default_user_photo)
                .into(profileImage)
        } ?: kotlin.run {

            Glide.with(requireContext())
                .load(FirebaseManager.mFirebaseUser!!.photoUrl)
                .placeholder(R.mipmap.default_user_photo)
                .into(profileImage)
        }

    }

    private fun setUserName() {
        user_name.text = FirebaseManager.currentUser?.user_name ?: "Adme User"
    }


}