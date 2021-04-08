package com.cookietech.namibia.adme.ui.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cookietech.namibia.adme.R
import kotlinx.android.synthetic.main.activity_client.*


class ClientActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.client_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        client_bottom_nav.setupWithNavController(navController)
    }
}