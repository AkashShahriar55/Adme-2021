package com.cookietech.namibia.adme.architecture.common.profile

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.interfaces.AuthConnectionCallback
import com.cookietech.namibia.adme.managers.FirebaseManager
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

class ContactInfoViewModel : ViewModel() {
    private val WEB_CLIENT_ID = "559137624340-5sdlvt9o0tbcl4lknp4ldbb9bheqs5pu.apps.googleusercontent.com"
    private val RC_SIGN_IN = 1

    //for facebook
    private var mCallbackManager: CallbackManager? = null
    private lateinit var repository: ContactInfoRepository

    var authConnectionCallback : AuthConnectionCallback? = null

    init{
        repository = ContactInfoRepository()
    }
    private var mGoogleSignInClient: GoogleSignInClient? = null
    fun signInWithGoogle(requireActivity: FragmentActivity) {
//        loginAndRegistrationManager.signInWithGoogle(requireActivity)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity, gso)
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requireActivity.startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    fun signInWithFacebook(requireActivity: FragmentActivity) {
        mCallbackManager = CallbackManager.Factory.create()
        Log.d("fb_login_debug", "mCallbackManager: ${mCallbackManager}")
        LoginManager.getInstance().logInWithReadPermissions(
            requireActivity, listOf(
                "email",
                "public_profile"
            )
        )
        LoginManager.getInstance()
            .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("fb_login_debug", "facebook:onSuccess: $loginResult")

                    //dialog.show()
                    firebaseAuthWithFacebook(loginResult.accessToken)
                }

                override fun onCancel() {
                    authConnectionCallback?.onAuthConnectionFailed()
                    Log.d("fb_login_debug", "facebook: onCancel")
                }

                override fun onError(error: FacebookException) {
                    authConnectionCallback?.onAuthConnectionFailed()
                    Log.d("fb_login_debug", "facebook:onError", error)
                }

            })

    }

    fun firebaseAuthWithFacebook(token: AccessToken) {
        repository.firebaseAuthWithFacebook(token)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                //Log.d("fb_login_debug", "signInWithCredential:success " + loginCallback)
                /*val user = auth.currentUser
                    updateUI(user)*/
                task.result.user?.let {
                    FirebaseManager.mFirebaseUser = it
                    authConnectionCallback?.onAuthConnectionSuccessful("facebook")
                }

            } else {
                // If sign in fails, display a message to the user.
                Log.w("fb_login_debug", "signInWithCredential:failure", task.exception)
                /*Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()*/
                //updateUI(null)
                authConnectionCallback?.onAuthConnectionFailed()
            }
        }

    }
    private fun firebaseAuthWithGoogle(idToken: String?) {
        repository.firebaseAuthWithGoogle(idToken)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                //Log.d("fb_login_debug", "signInWithCredential:success " + loginCallback)
                /*val user = auth.currentUser
                    updateUI(user)*/
                task.result.user?.let {
                    FirebaseManager.mFirebaseUser = it
                    authConnectionCallback?.onAuthConnectionSuccessful("google")
                }

            } else {
                // If sign in fails, display a message to the user.
                Log.w("fb_login_debug", "signInWithCredential:failure", task.exception)
                /*Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()*/
                //updateUI(null)
                authConnectionCallback?.onAuthConnectionFailed()
            }
        }

    }

    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("fb_login_debug", "onActivityResult: ")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("google_login_debug", "firebaseAuthWithGoogle:" + account.id)

                /** Show Dialog**/
                //dialog.show()
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("google_login_debug", "Google sign in failed"+ e.status)

                // ...
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            /**  Implement Facebook SDK**/
            Log.d("fb_login_debug", "mCallbackManager: ${mCallbackManager}")
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }


}