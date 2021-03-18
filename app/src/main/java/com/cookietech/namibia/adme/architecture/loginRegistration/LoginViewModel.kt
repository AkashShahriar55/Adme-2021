package com.cookietech.namibia.adme.architecture.loginRegistration

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.facebook.AccessToken

class LoginViewModel : ViewModel() {

    val firebaseManager = FirebaseManager()
    var loginAndRegistrationManager: LoginAndRegistrationManager
    init {
        loginAndRegistrationManager = LoginAndRegistrationManager(firebaseManager)
    }


    fun firebaseAuthWithGoogle(idToken: String?) {
        loginAndRegistrationManager.firebaseAuthWithGoogle(idToken)
    }

    fun firebaseAuthWithFacebook(token: AccessToken) {
        loginAndRegistrationManager.firebaseAuthWithFacebook(token)

    }

    fun signInWithGoogle(requireActivity: FragmentActivity) {
        loginAndRegistrationManager.signInWithGoogle(requireActivity)
    }

    fun signInWithFacebook(requireActivity: FragmentActivity) {
        loginAndRegistrationManager.signInWithFacebook(requireActivity)
    }



}