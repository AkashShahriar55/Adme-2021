package com.cookietech.namibia.adme.architecture.common.notification

import android.util.Log
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class NotificationRepository {

    fun fetchNotifications(listener: EventListener<QuerySnapshot>): ListenerRegistration {
        Log.d("notfif_debug", "fetchNotifications: ")

        return FirebaseManager.mUserRef.document(FirebaseManager.currentUser!!.user_id).collection("notification_list").whereIn("mode",listOf(SharedPreferenceManager.user_mode, AppComponent.MODE_BOTH)).addSnapshotListener(listener)
    }
}