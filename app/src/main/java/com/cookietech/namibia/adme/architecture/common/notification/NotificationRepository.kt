package com.cookietech.namibia.adme.architecture.common.notification

import android.util.Log
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class NotificationRepository {

    fun fetchNotifications(listener: EventListener<QuerySnapshot>): ListenerRegistration {
        Log.d("notfif_debug", "fetchNotifications: ")

        return FirebaseManager.mUserRef.document(FirebaseManager.currentUser!!.user_id).collection("notification_list")
            .orderBy("time", Query.Direction.DESCENDING)
            .whereIn("mode",listOf(SharedPreferenceManager.user_mode, AppComponent.MODE_BOTH))
           /* */
            .addSnapshotListener(listener)
    }

    fun updateIssenStatus(notificationId: String): Task<Void> {
        Log.d("isSeen_debug", "updateIssenStatus: $notificationId")
        return FirebaseManager.mUserRef
            .document(FirebaseManager.currentUser!!.user_id)
            .collection("notification_list").document(notificationId)
            .update("isSeen", true)
    }

    fun updateUnreadNotificationStatus(key: String): Task<Void> {
        val data = hashMapOf(key to false)
        return FirebaseManager.mUserRef
            .document(FirebaseManager.currentUser!!.user_id)
            .set(data, SetOptions.merge())
    }
}