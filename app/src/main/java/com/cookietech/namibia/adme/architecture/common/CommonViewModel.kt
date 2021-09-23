package com.cookietech.namibia.adme.architecture.common

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel

class CommonViewModel : ViewModel() {

    var commonActivityCallbacks: CommonActivityCallbacks? = null

    fun addCommonActivityCallback(commonActivityCallbacks: CommonActivityCallbacks){
        this.commonActivityCallbacks = commonActivityCallbacks
    }

    fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        commonActivityCallbacks?.processActivityResult(requestCode,resultCode,data)

    }

    interface CommonActivityCallbacks{
        fun processActivityResult(requestCode: Int,resultCode: Int,data: Intent?)
    }
}