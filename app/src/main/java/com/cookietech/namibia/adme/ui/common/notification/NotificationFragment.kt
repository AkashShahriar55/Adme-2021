package com.cookietech.namibia.adme.ui.common.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.notification.NotificationViewModel
import com.cookietech.namibia.adme.ui.serviceProvider.today.appointment.AppointmentDetailsActivity
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class NotificationFragment : Fragment() {

    private val notificationViewModel : NotificationViewModel by viewModels()
    private var adapter: NotificationAdapter? = null
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /* arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
        callApis()

    }

    private fun callApis() {
        getNotifications()
        updateUnreadNotificationStatus()
    }

    private fun updateUnreadNotificationStatus() {
        notificationViewModel.updateUnreadNotificationStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        initializeRV()
        initializeObserver()


        back_button.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initializeObserver() {
        notificationViewModel.notificationList.observe(viewLifecycleOwner, { notifList ->
            adapter?.apply {
                notificationList = notifList
            }
            startPostponedEnterTransition()
        })
    }

    private fun getNotifications() {
        notificationViewModel.getNotifications()
    }

    private fun initializeRV() {
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
         adapter = NotificationAdapter(object : NotificationAdapter.NotificationClickListener {
             override fun onNotificationClicked(appointmentId: String, notificationId: String?) {
                 //Log.d("notif_debug", "onNotificationClicked: $appointmentId")

                 val bundle = Bundle()
                 bundle.putParcelable("appointment", null)
                 bundle.putString("appointment_id", appointmentId)
                 //findNavController().navigate(R.id.my_deals_to_appointment_details,bundle)
                 val intent = Intent(requireContext(), AppointmentDetailsActivity::class.java)
                 intent.putExtras(bundle)
                 startActivity(intent)
                 //notificationViewModel.updateIssenStatus(notificationId)
                 workerScope.launch {
                     notificationViewModel.updateIssenStatus(notificationId)
                 }
             }

         }, requireContext())
        notificationViewModel.notificationList.value?.apply {
            adapter?.notificationList = this
        }
        rv_notification.layoutManager = mLayoutManager
        rv_notification.itemAnimator = DefaultItemAnimator()
        rv_notification.adapter = adapter
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
               /* arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }
}