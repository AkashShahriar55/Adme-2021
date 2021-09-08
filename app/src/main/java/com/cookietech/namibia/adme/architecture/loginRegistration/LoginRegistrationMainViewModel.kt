package com.cookietech.namibia.adme.architecture.loginRegistration

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.interfaces.FCMTokenCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager

class LoginRegistrationMainViewModel: ViewModel() {
    var loginAndRegistrationManager: LoginAndRegistrationManager = LoginAndRegistrationManager()

    var activityCallbacks:ActivityCallbacks? = null

    fun addActivityCallback(activityCallbacks: ActivityCallbacks){
        this.activityCallbacks = activityCallbacks
    }

    fun tryToLogin(callback: LoginAndRegistrationManager.UserCreationCallback): Boolean {
        return loginAndRegistrationManager.checkIfAlreadyLoggedIn(callback)
    }

    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityCallbacks?.processActivityResult(requestCode,resultCode,data)
    }


    fun updateFCMToken() {

        FirebaseManager.getFCMToken(object : FCMTokenCallback{
            override fun onTokenGenerationSuccess(token: String) {
                FirebaseManager.mFirebaseUser?.apply {
                    loginAndRegistrationManager.updateFCMToken(token,uid)
                }
            }
            override fun onTokenGenerationFailed() {
                Log.d("FCM_debug", "onTokenGenerationFailed: ")
            }

        })

    }


    interface ActivityCallbacks{
        fun processActivityResult(requestCode: Int,resultCode: Int,data: Intent?)
    }


}