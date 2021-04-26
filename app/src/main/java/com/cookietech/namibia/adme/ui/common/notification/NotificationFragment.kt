package com.cookietech.namibia.adme.ui.common.notification

import android.app.Notification
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.notification.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment() {

    private val notificationViewModel : NotificationViewModel by viewModels()
    private var adapter: NotificationAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /* arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
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

        initializeRV()
        initializeObserver()
        getNotifications()
    }

    private fun initializeObserver() {
        notificationViewModel.notificationList.observe(viewLifecycleOwner, { notifList ->
            adapter?.apply {
               notificationList = notifList
            }
        })
    }

    private fun getNotifications() {
        notificationViewModel.getNotifications()
    }

    private fun initializeRV() {
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
         adapter = NotificationAdapter(object : NotificationAdapter.NotificationClickListener{
            override fun onNotificationClicked(notification: Notification) {

            }
        })
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