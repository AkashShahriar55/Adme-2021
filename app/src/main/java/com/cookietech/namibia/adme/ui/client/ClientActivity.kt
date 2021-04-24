package com.cookietech.namibia.adme.ui.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cookietech.namibia.adme.R
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.android.synthetic.main.activity_service_provider.*


class ClientActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
       /* val callback = onBackPressedDispatcher.addCallback(this) {
            Log.d("navigation_debug", "onBackPressedDispatcher: ${client_bottom_nav.selectedItemId}")
            if(client_bottom_nav.selectedItemId == R.id.homeFragment){
                finishAffinity()
            }else{
                client_bottom_nav.selectedItemId = R.id.homeFragment
            }
        }*/
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.client_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        client_bottom_nav.setupWithNavController(navController)
    }


    override fun onNavigateUp(): Boolean {
        val value = super.onNavigateUp()
        Log.d("navigation_debug", "onNavigateUp: $value")
        return value
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("navigation_debug", "onBackPressed: " + supportFragmentManager.fragments[supportFragmentManager.fragments.size-2])
    }


    override fun onSupportNavigateUp(): Boolean {
        val value = super.onSupportNavigateUp()
        Log.d("navigation_debug", "onSupportNavigateUp: $value")
        return value
        
    }
}