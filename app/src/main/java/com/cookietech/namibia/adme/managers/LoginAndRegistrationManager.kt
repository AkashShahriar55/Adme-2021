package com.cookietech.namibia.adme.managers

import com.google.firebase.auth.FirebaseUser

class LoginAndRegistrationManager(private val firebaseManager: FirebaseManager) {

    fun checkIfAlreadyLoggedIn(): Boolean {
        val currentUser: FirebaseUser? = firebaseManager.mAuth.currentUser
        return currentUser != null
    }
}