package com.cookietech.namibia.adme.architecture.loginRegistration

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.cookietech.namibia.adme.views.CustomToast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.*

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private var mResendToken: ForceResendingToken? = null
    private var mVerificationId: String? = null
    private val loginAndRegistrationManager = LoginAndRegistrationManager()
    public var registrationCallbacks:RegistrationCallbacks? = null
    private var isLinkWithUser = false
    val mCallbacks = object : OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            if(isLinkWithUser){
                linkWithUser(phoneAuthCredential)
            }else{
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            Log.d("akash_debug", "onVerificationCompleted: ")
        }

        override fun onCodeAutoRetrievalTimeOut(s: String) {
            super.onCodeAutoRetrievalTimeOut(s)
            Log.d("akash_debug", "onCodeAutoRetrievalTimeOut: ")
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            Log.d("akash_debug", "onVerificationFailed: $exception")
            Log.d("akash_debug", "onVerificationFailed: $exception")
            if (exception.javaClass == FirebaseAuthInvalidCredentialsException::class.java) {
                Log.d(
                    "akash_debug",
                    "onVerificationFailed: invalid credential"
                )
                CustomToast.makeErrorToast(
                    application,
                    "Please enter a valid no!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (exception.javaClass == FirebaseTooManyRequestsException::class.java) {
                Log.d(
                    "akash_debug",
                    "onVerificationFailed: too many request"
                )
                CustomToast.makeErrorToast(
                    application,
                    "Too many request.Try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
            Log.d("akash_debug", "onCodeSent: ")
            CustomToast.makeSuccessToast(
                application,
                "A Verification Code is sent.",
                Toast.LENGTH_SHORT
            ).show()


//
//            // Save verification ID and resending token so we can use them later
//            codeSent = true

            mVerificationId = verificationId
            mResendToken = token
            registrationCallbacks?.onCodeSend()

        }
    }

    private fun linkWithUser(phoneAuthCredential: PhoneAuthCredential) {
        loginAndRegistrationManager.linkWithUser(phoneAuthCredential
        ) { task ->
            if (task.isSuccessful) {

                task.result.user?.let {
                    Log.d("akash_debug", "linkWithUser: " + it.phoneNumber)
                    FirebaseManager.mFirebaseUser = it
                    registrationCallbacks?.onLoginSuccessful()
                }

            } else {
                // Sign in failed, display a message and update the UI
                registrationCallbacks?.onLoginFailed()
                // Snackbar.make(contextView,"Error: " + Objects.requireNonNull(task.getException()).getMessage(),Snackbar.LENGTH_SHORT).show();
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    CustomToast.makeErrorToast(
                            getApplication(),
                            "Verification code is invalid",
                            Toast.LENGTH_LONG
                    ).show()
//                        resetCodeFields()
                } else {
                    CustomToast.makeErrorToast(
                            getApplication(),
                            "Error: " + Objects.requireNonNull(task.exception)?.message,
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        loginAndRegistrationManager.signInWithPhoneAuthCredential(phoneAuthCredential
        ) { task ->
            if (task.isSuccessful) {
                task.result.user?.let {
                    Log.d("akash_debug", "signInWithPhoneAuthCredential: " + it.phoneNumber)
                    FirebaseManager.mFirebaseUser = it
                    registrationCallbacks?.onLoginSuccessful()
                }
            } else {
                // Sign in failed, display a message and update the UI

                // Snackbar.make(contextView,"Error: " + Objects.requireNonNull(task.getException()).getMessage(),Snackbar.LENGTH_SHORT).show();
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    CustomToast.makeErrorToast(
                        getApplication(),
                        "Verification code is invalid",
                        Toast.LENGTH_LONG
                    ).show()
//                        resetCodeFields()
                } else {
                    CustomToast.makeErrorToast(
                        getApplication(),
                        "Error: " + Objects.requireNonNull(task.exception)?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    public fun signInWithPhoneAuthCredentialForCode(verificationCodeString:String){
            mVerificationId?.let {
                val credential =PhoneAuthProvider.getCredential(it, verificationCodeString.toString())
                signInWithPhoneAuthCredential(credential)
            }
    }

    public fun linkWithPhoneAuthCredentialForCode(verificationCodeString:String){
        mVerificationId?.let {
            val credential =PhoneAuthProvider.getCredential(it, verificationCodeString.toString())
            linkWithUser(credential)
        }
    }

    fun sendVerificationCode(phoneNo: String, activity: Activity,isLinkWithUser: Boolean){
        this.isLinkWithUser = isLinkWithUser
        loginAndRegistrationManager.verifyPhoneNo(phoneNo, mCallbacks, activity)
    }


    interface RegistrationCallbacks {
        fun onCodeSend()
        fun onLoginSuccessful()
        fun onLoginFailed()
    }
}