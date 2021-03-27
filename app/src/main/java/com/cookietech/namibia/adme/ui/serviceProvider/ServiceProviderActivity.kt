package com.cookietech.namibia.adme.ui.serviceProvider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cookietech.namibia.adme.R
import kotlinx.android.synthetic.main.activity_service_provider.*

class ServiceProviderActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.service_provider_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        service_provider_bottom_nav.setupWithNavController(navController)
    }
}