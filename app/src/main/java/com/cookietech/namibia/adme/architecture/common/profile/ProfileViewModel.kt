package com.cookietech.namibia.adme.architecture.common.profile

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.ui.loginRegistration.LoginRegistrationActivity
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener

class ProfileViewModel : ViewModel() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val gso: GoogleSignInOptions
    private val WEB_CLIENT_ID = "559137624340-5sdlvt9o0tbcl4lknp4ldbb9bheqs5pu.apps.googleusercontent.com"

    init {

        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }

    fun logout(context: Context) {


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso!!)
        FirebaseManager.mAuth.signOut()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        when {
            account != null -> {
                /*user already signed in*/
                googleSignOut(context)
            }
            isLoggedIn -> {
                facebookSignOut(context)
            }
            else -> {
                val intent = Intent(context, LoginRegistrationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                context.startActivity(intent)

            }
        }

        FirebaseManager.currentUser = null
        FirebaseManager.mFirebaseUser = null

    }

    private fun googleSignOut(context: Context) {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener {
            val intent = Intent(context, LoginRegistrationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            context.startActivity(intent)
        }
    }

    private fun facebookSignOut(context: Context) {
        LoginManager.getInstance().logOut()
        //mAuth.signOut();
        val intent = Intent(context, LoginRegistrationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        context.startActivity(intent)
    }
}