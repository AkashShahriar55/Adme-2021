package com.cookietech.namibia.adme.ui.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.CommonViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.android.synthetic.main.activity_service_provider.*
import kotlinx.android.synthetic.main.chat_activity_main.*

@AndroidEntryPoint
class ClientActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val commonViewModel : CommonViewModel by viewModels()

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


        client_bottom_nav.setOnItemReselectedListener{
            Log.d("nav_debug", "onCreate: ${it.itemId} ${R.id.home_navigation} ${R.id.chat_navigation} ${R.id.mydeals_navigation} ${R.id.profile_navigation}")
            when(it.itemId){

                R.id.home_navigation-> {

                    navController.popBackStack(R.id.homeFragment,false)

                }
                R.id.chat_navigation-> {

                    navController.popBackStack(R.id.leaderBoardFragment,false)
                }
                R.id.mydeals_navigation-> {

                    navController.popBackStack(R.id.myDealsFragment,false)
                }
                R.id.profile_navigation-> {
                    navController.popBackStack(R.id.service_provider_profileFragment,false)
                }
            }
        }

    }


    override fun onNavigateUp(): Boolean {
        val value = super.onNavigateUp()
        Log.d("navigation_debug", "onNavigateUp: $value")
        return value
    }


    override fun onBackPressed() {
        val currentFragment = navController.currentDestination?.id
        Log.d("nav_debug", "onBackPressed: $currentFragment ${R.id.homeFragment} " + (currentFragment == R.id.homeFragment) +" " + (client_bottom_nav.selectedItemId == R.id.homeFragment))
        if(client_bottom_nav.selectedItemId == R.id.home_navigation && currentFragment == R.id.homeFragment ){
            AlertDialog.Builder(this@ClientActivity).setMessage("Do you want to exit?")
                    .setPositiveButton("Yes"
                    ) { p0, p1 -> finishAffinity() }.setNegativeButton("No"
                    ) { p0, p1 -> p0?.dismiss() }.create().show()
        }else{
            Log.d("nav_debug", "onBackPressed: back " + supportFragmentManager.fragments[supportFragmentManager.fragments.size-2])
            super.onBackPressed()
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val value = super.onSupportNavigateUp()
        Log.d("navigation_debug", "onSupportNavigateUp: $value")
        return value
        
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        commonViewModel.processActivityResult(requestCode,resultCode,data)


    }
}