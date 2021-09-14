package com.cookietech.namibia.adme.ui.client.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.chatmodule.view.FirebaseViewModel
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_marker_click_details.*
import kotlinx.android.synthetic.main.layout_marker_click_details.tv_category
import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.NavDeepLinkRequest




@AndroidEntryPoint
class MarkerClickDetailsDialog(): BottomSheetDialogFragment() {

    private var messageClicked: Boolean = false
    var service:ServicesPOJO? = null

    private val firebaseVm: FirebaseViewModel by viewModels({ requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            service = getParcelable("data")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_marker_click_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpClicks()
    }

    private fun setUpClicks() {
        /**View Profile**/
        btn_view_profile.setOnClickListener{
            val bundle = Bundle()
            bundle.putParcelable("service", service)

           //findNavController().navigate(R.id.marker_dialog_to_sp_activity,bundle)
            val intent = Intent(context, ServiceProviderDetailsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            }

            intent.putExtras(bundle)
            startActivity(intent)
        }


        btn_message.setOnClickListener {
            Log.d("akash_chat_debug", "setUpClicks: "+service?.user_ref)
            messageClicked = true
            service?.user_ref?.let { userId ->


                firebaseVm.getUser(userId)
            }
        }

        firebaseVm.particularUserData.observe(viewLifecycleOwner, Observer{
            it?.let { user->
                if(messageClicked){
                    firebaseVm.setReceiver(user)
                    val request = NavDeepLinkRequest.Builder
                        .fromUri("android-app://example.google.app/chat_fragment".toUri())
                        .build()
                    findNavController().navigate(request)

                    messageClicked = false
                }

            }
        })

        firebaseVm.userDataStatus.observe(viewLifecycleOwner, Observer {
            Log.d("akash_chat_debug", "setUpClicks: "+it)
        })

    }

    private fun setUpViews() {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
        service?.apply {

            tv_username.text = user_name
            tv_category.text = category
            tv_details.text = description

            Glide.with(requireContext())
                .load(pic_url)
                .into(marker_profile_image)

            rating_bar.rating = rating.toFloat()
            tv_work_done.text = reviews

            Log.d("service_debug", "setUpViews: $latitude $longitude")

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): MarkerClickDetailsDialog {
            val fragment = MarkerClickDetailsDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

}