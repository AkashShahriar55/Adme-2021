package com.cookietech.namibia.adme.ui.serviceProvider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.app.NavUtils
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.ServiceProviderViewModel
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_service_provider.*
import java.lang.Exception

@AndroidEntryPoint
class ServiceProviderActivity : AppCompatActivity() {

    val serviceProviderViewModel: ServiceProviderViewModel by viewModels()
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider)
        // This callback will only be called when MyFragment is at least Started.
        val callback = onBackPressedDispatcher.addCallback(this) {
            Log.d("navigation_debug", "onBackPressedDispatcher: ${service_provider_bottom_nav.selectedItemId}")
            if(service_provider_bottom_nav.selectedItemId == R.id.todayFragment){
                finishAffinity()
            }else{
                service_provider_bottom_nav.selectedItemId = R.id.todayFragment
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.service_provider_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        service_provider_bottom_nav.setupWithNavController(navController)

    }

    override fun onNavigateUp(): Boolean {
        val value = super.onNavigateUp()
        Log.d("navigation_debug", "onNavigateUp: $value")
        return value
    }

    override fun onBackPressed() {
        Log.d("navigation_debug", "onBackPressed: " )

        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val value = super.onSupportNavigateUp()
        Log.d("navigation_debug", "onBackPressed: $value" )
        return value
    }
}