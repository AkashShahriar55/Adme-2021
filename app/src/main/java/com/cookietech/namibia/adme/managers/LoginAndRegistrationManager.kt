package com.cookietech.namibia.adme.managers

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

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
    /** Firebase Authentication With Google**/
    fun firebaseAuthWithGoogle(idToken: String?): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return firebaseManager.mAuth.signInWithCredential(credential)
    }

    /** Firebase Authentication With Facebook**/
    fun firebaseAuthWithFacebook(token: AccessToken): Task<AuthResult> {
        Log.d("fb_login_debug", "handleFacebookAccessToken: called")
        val credential = FacebookAuthProvider.getCredential(token.token)
        return firebaseManager.mAuth.signInWithCredential(credential)
    }







}