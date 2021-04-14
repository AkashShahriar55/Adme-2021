package com.cookietech.namibia.adme.ui.common.profile

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.profile.ProfileViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.ui.client.ClientActivity
import com.cookietech.namibia.adme.ui.serviceProvider.ServiceProviderActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    val profileViewModel : ProfileViewModel by viewModels()

    init {

    }
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
    }

    private fun logout() {

        profileViewModel.logout(requireContext())

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
    }

    private fun initialFields() {
        setProfilePhoto()
        setUserName()
        setMemberSince()
    }

    private fun setMemberSince() {
        val simpleDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val registration_date : String = simpleDate.format(FirebaseManager.currentUser!!.registration_date!!.toDate())

        //Log.d("setMemberSince", "setMemberSince: " + registraion_date)
        sinceTime.text = "Member Since $registration_date"


    }

    private fun setProfilePhoto() {
        FirebaseManager.currentUser!!.profile_image_url?.let {
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