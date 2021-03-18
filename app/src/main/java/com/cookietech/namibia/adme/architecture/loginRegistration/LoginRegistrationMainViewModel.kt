package com.cookietech.namibia.adme.architecture.loginRegistration

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.google.firebase.auth.FirebaseUser

class LoginRegistrationMainViewModel: ViewModel() {
    val firebaseManager = FirebaseManager()
    var loginAndRegistrationManager: LoginAndRegistrationManager
    init {
        loginAndRegistrationManager = LoginAndRegistrationManager(firebaseManager)
    }

    fun checkIfAlreadyLoggedIn(): Boolean {
        return loginAndRegistrationManager.checkIfAlreadyLoggedIn()
    }

    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginAndRegistrationManager.processActivityResult(requestCode,resultCode,data)
    }
}