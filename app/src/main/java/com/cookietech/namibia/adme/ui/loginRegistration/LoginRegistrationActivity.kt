package com.cookietech.namibia.adme.ui.loginRegistration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel


class LoginRegistrationActivity : AppCompatActivity() {

    val loginRegistrationMainViewModel: LoginRegistrationMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginRegistrationMainViewModel.processActivityResult(requestCode,resultCode,data)
    }
}