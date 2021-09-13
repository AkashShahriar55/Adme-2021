package com.cookietech.namibia.adme.architecture.common.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.models.NotificationPOJO
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.firebase.firestore.ListenerRegistration
import io.reactivex.rxjava3.internal.operators.single.SingleLift

class NotificationViewModel : ViewModel() {

    private lateinit var notificationListenerRegistration: ListenerRegistration
    private val  notificationRepository = NotificationRepository()
    val notificationList = SingleLiveEvent<ArrayList<NotificationPOJO>>()

    fun getNotifications(){
        Log.d("notfif_debug", "getNotifications: ")
       notificationListenerRegistration = notificationRepository.fetchNotifications { snapshot, error ->

           error?.apply {
               Log.d("notfif_debug", "fetchNotifications: error" + error.message)
               return@apply
           }


           snapshot?.let { documents ->
               val list = arrayListOf<NotificationPOJO>()

               for (document in documents){
                   Log.d("notif_debug", "getNotifications doc: ${document.id} ${document.get("isSeen")}")
                   val notification = document.toObject(NotificationPOJO::class.java)
                   notification.id = document.id
                   notification.isSeen = document.get("isSeen") as Boolean?
                   Log.d("notif_debug", "getNotifications: ${notification.id} ${notification.isSeen}")
                   notification.let { list.add(it) }

               }
               notificationList.value = list
               Log.d("notif_debug", "notification: ${list.size}")
           }



       }
    }

    override fun onCleared() {
        super.onCleared()
        notificationListenerRegistration.remove()
    }

    fun updateIssenStatus(notificationId: String?) {
        Log.d("isSeen_debug", "updateIssenStatus: called")
        notificationId?.let {
            notificationRepository.updateIssenStatus(it)
                .addOnSuccessListener {
                    Log.d("isSeen_debug", "updateIssenStatus: success")
                }
                .addOnFailureListener {
                    Log.d("isSeen_debug", "updateIssenStatus: failure")
                }
        }
    }
}