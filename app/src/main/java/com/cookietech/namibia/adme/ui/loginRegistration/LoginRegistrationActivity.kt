package com.cookietech.namibia.adme.ui.loginRegistration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel
import com.cookietech.namibia.adme.chatmodule.utils.states.AuthenticationState
import com.cookietech.namibia.adme.chatmodule.view.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegistrationActivity : AppCompatActivity() {

    val loginRegistrationMainViewModel: LoginRegistrationMainViewModel by viewModels()
    private val firebaseVm: FirebaseViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        firebaseVm.authenticationState.observe(this, Observer {
            Log.d("akash_chat_debug",
                ("observeAuthState: " + it)
            )
//            when (it) {
//                is AuthenticationState.Authenticated -> onAuthenticated()
//                is AuthenticationState.Unauthenticated -> onUnauthenticated()
//                is AuthenticationState.InvalidAuthentication -> TODO()
//            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginRegistrationMainViewModel.processActivityResult(requestCode,resultCode,data)


    }
}