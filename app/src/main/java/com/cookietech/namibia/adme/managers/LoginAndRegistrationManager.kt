package com.cookietech.namibia.adme.managers

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginAndRegistrationManager(private val firebaseManager: FirebaseManager) {

    fun checkIfAlreadyLoggedIn(): Boolean {
        val currentUser: FirebaseUser? = firebaseManager.mAuth.currentUser
        return currentUser != null
    }


    fun verifyPhoneNo(phoneNo:String,callback:PhoneAuthProvider.OnVerificationStateChangedCallbacks,activity: Activity){
        val options = PhoneAuthOptions.newBuilder(firebaseManager.mAuth)
            .setPhoneNumber(phoneNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun signInWithPhoneAuthCredential(
        phoneAuthCredential: PhoneAuthCredential,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        firebaseManager.mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(onCompleteListener)
    }
}