package com.cookietech.namibia.adme.ui.serviceProvider.today

import android.app.Service
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
import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.NavDeepLinkRequest
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.architecture.client.home.SearchServiceAdapter
import kotlinx.android.synthetic.main.fragment_search_services.*


@AndroidEntryPoint
class AppointmentsDetailsDialog(): BottomSheetDialogFragment() {

    private var adapter: SearchServiceAdapter? = null
    private var messageClicked: Boolean = false
    var service:ArrayList<ServicesPOJO>? = null
    var userRef:String? = null

    private val firebaseVm: FirebaseViewModel by viewModels({ requireActivity() })



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            service = getParcelableArrayList("data")
            userRef = getString("user_ref")
            Log.d("dialog_debug", "onCreate: " + service?.size)
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
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        setUpViews()
        setUpClicks()
    }

    private fun setUpClicks() {
        /**View Profile**/
//        btn_view_profile.setOnClickListener{
//            val bundle = Bundle()
//            bundle.putParcelable("service", service)
//
//           //findNavController().navigate(R.id.marker_dialog_to_sp_activity,bundle)
//            val intent = Intent(context, ServiceProviderDetailsActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//            }
//
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }


        btn_message.setOnClickListener {
            Log.d("akash_chat_debug", "setUpClicks: "+userRef)
            messageClicked = true
            userRef?.let { userId ->


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


    private fun initializeRV() {
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

        adapter = SearchServiceAdapter(
            java.util.ArrayList<ServicesPOJO>(),
            requireContext(),
            object : SearchServiceAdapter.SearchItemCallback{
                override fun onSearchItemClicked(service: ServicesPOJO) {
                    val bundle = Bundle()
                    bundle.putParcelable("service", service)

                    //findNavController().navigate(R.id.marker_dialog_to_sp_activity,bundle)
//                    val intent = Intent(context, ServiceProviderDetailsActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//                    }

//                    intent.putExtras(bundle)
//                    startActivity(intent)
                }

            })
        rv_service_holder.layoutManager = mLayoutManager
        rv_service_holder.itemAnimator = DefaultItemAnimator()
        rv_service_holder.adapter = adapter
    }

    private fun setUpViews() {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
        service?.apply {
            initializeRV()
            adapter!!.resetSearchData(this)

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): AppointmentsDetailsDialog {
            val fragment = AppointmentsDetailsDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

}