package com.cookietech.namibia.adme.ui.loginRegistration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginViewModel
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*
import kotlin.math.log


class LoginFragment : Fragment() {

    val loginViewModel : LoginViewModel by viewModels()

    init {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariables()
        initializeClicks()
    }

    private fun initializeVariables() {
        /**Get Google client**/
        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    }

    private fun initializeClicks() {
        /**Google Login**/
        login_google_btn.setOnClickListener{
            //Toast.makeText(requireContext(),"Clicked",Toast.LENGTH_SHORT).show()
            signInWithGoogle()
        }

        /**Facebook Login**/
        login_facebook_btn.setOnClickListener{
            signInWithFacebook()
        }


    }

    private fun signInWithFacebook() {

        Log.d("fb_login_debug", "signInWithFacebook: Called")

        loginViewModel.signInWithFacebook(requireActivity())
    }



    private fun signInWithGoogle() {
        loginViewModel.signInWithGoogle(requireActivity())

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}