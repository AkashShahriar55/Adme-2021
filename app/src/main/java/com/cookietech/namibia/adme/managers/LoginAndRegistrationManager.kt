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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginAndRegistrationManager(private val firebaseManager: FirebaseManager) {

    private var mGoogleSignInClient: GoogleSignInClient? = null

    //Facebook Callback manager


    companion object {
        private val RC_SIGN_IN = 1
        private val WEB_CLIENT_ID = "559137624340-5sdlvt9o0tbcl4lknp4ldbb9bheqs5pu.apps.googleusercontent.com"
        private var mCallbackManager: CallbackManager? = null
    }


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
    fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseManager.mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("google_login_debug", "signInWithCredential:success")
                    //val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("google_login_debug", "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }

    }

    /** Firebase Authentication With Facebook**/
    fun firebaseAuthWithFacebook(token: AccessToken) {
        Log.d("fb_login_debug", "handleFacebookAccessToken: called")
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseManager.mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("fb_login_debug", "signInWithCredential:success")
                    /*val user = auth.currentUser
                    updateUI(user)*/
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("fb_login_debug", "signInWithCredential:failure", task.exception)
                    /*Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()*/
                    //updateUI(null)
                }
            }
    }

    /** SignIn With Google**/
    fun signInWithGoogle(requireActivity: FragmentActivity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity, gso)
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requireActivity.startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    /** SignInWith Facebook**/
    fun signInWithFacebook(requireActivity: FragmentActivity) {
        mCallbackManager = CallbackManager.Factory.create()
        Log.d("fb_login_debug", "mCallbackManager: $mCallbackManager")
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
                    Log.d("fb_login_debug", "facebook: onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("fb_login_debug", "facebook:onError", error)
                }

            })
    }

    /** Process Activity Result After Selecting gmail or  fb account**/
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
                Log.d("google_login_debug", "Google sign in failed", e)
                // ...
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            /**  Implement Facebook SDK**/
            Log.d("fb_login_debug", "mCallbackManager: $mCallbackManager")
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }

    }


}