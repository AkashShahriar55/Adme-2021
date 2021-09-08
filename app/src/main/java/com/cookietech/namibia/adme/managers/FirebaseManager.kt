package com.cookietech.namibia.adme.managers

import android.provider.Settings.Global.getString
import android.util.Log
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.interfaces.FCMTokenCallback
import com.cookietech.namibia.adme.models.UserPOJO
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseManager {
    private const val USER_COLLECTION_ID = "Adme_User"
    private const val CATEGORY_COLLECTION = "Adme_Service_Category"
    private const val SERVICE_LIST = "Adme_Service_list"
    private const val APPOINTMENT_LIST= "Adme_Appointment_list"

    const val STORAGE_FOLDER_SERVICE_PORTFOLIO = "service_portfolio"
    const val STORAGE_FOLDER_SERVICE_INVOICE = "service_invocice"


    val mDataBase: FirebaseFirestore = FirebaseFirestore.getInstance()

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val mFunctions: FirebaseFunctions = FirebaseFunctions.getInstance()
    var mUserRef: CollectionReference
    var currentUser:UserPOJO? = null
    var mFirebaseUser:FirebaseUser? = null
    var mCategoryReference:CollectionReference
    var mPortfolioImageReference: StorageReference = mStorage.reference.child(STORAGE_FOLDER_SERVICE_PORTFOLIO)
    var mInvoiceImageReference: StorageReference = mStorage.reference.child(STORAGE_FOLDER_SERVICE_INVOICE)
    var mServiceListReference:CollectionReference
    var mAppointmentReference:CollectionReference

    init {
        mUserRef = mDataBase.collection(USER_COLLECTION_ID)
        mCategoryReference = mDataBase.collection(CATEGORY_COLLECTION)
        mFirebaseUser = mAuth.currentUser
        mServiceListReference = mDataBase.collection(SERVICE_LIST)
        mAppointmentReference = mDataBase.collection(APPOINTMENT_LIST)
    }

    fun getFCMToken(callback : FCMTokenCallback){


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                callback.onTokenGenerationFailed()
                Log.d("FCM_debug", "getFCMToken: error : ${task.exception?.message}")
                return@OnCompleteListener

            }

            // Get new FCM registration token

            //Log.d("FCM_debug", "getFCMToken: $token")
            callback.onTokenGenerationSuccess(task.result)


        })

    }

}