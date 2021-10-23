package com.cookietech.namibia.adme.ui.serviceProvider

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.CommonViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.ServiceProviderViewModel
import com.cookietech.namibia.adme.interfaces.ServiceProviderDataCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.android.synthetic.main.activity_service_provider.*
import java.lang.Exception

@AndroidEntryPoint
class ServiceProviderActivity : AppCompatActivity() {

    val serviceProviderViewModel: ServiceProviderViewModel by viewModels()
    private val commonViewModel : CommonViewModel by viewModels()
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider)
        // This callback will only be called when MyFragment is at least Started.
//        val callback = onBackPressedDispatcher.addCallback(this) {
//            Log.d("navigation_debug", "onBackPressedDispatcher: ${service_provider_bottom_nav.selectedItemId}")
//            if(service_provider_bottom_nav.selectedItemId == R.id.todayFragment){
//                Log.d("navigation_debug", "onCreate: "+   navController.currentBackStackEntry?.id)
//
//                AlertDialog.Builder(this@ServiceProviderActivity).setMessage("Do you want to exit?")
//                    .setPositiveButton("Yes"
//                    ) { p0, p1 -> finishAffinity() }.setNegativeButton("No"
//                    ) { p0, p1 -> p0?.dismiss() }.create().show()
//
//            }else{
//                service_provider_bottom_nav.selectedItemId = R.id.todayFragment
//            }
//        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.service_provider_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        service_provider_bottom_nav.setupWithNavController(navController)

        service_provider_bottom_nav.setOnItemReselectedListener{
            Log.d("nav_debug", "onCreate: ${it.itemId} ${R.id.today_navigation} ${R.id.chat_navigation} ${R.id.income_navigation} ${R.id.profile_navigation}")
            when(it.itemId){

                R.id.today_navigation-> {

                    navController.popBackStack(R.id.todayFragment,false)

                }
                R.id.chat_navigation-> {

                    navController.popBackStack(R.id.leaderBoardFragment,false)
                }
                R.id.income_navigation-> {

                    navController.popBackStack(R.id.incomeFragment,false)
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
        Log.d("navigation_debug", "onBackPressed: " )
        val currentFragment = navController.currentDestination?.id
        Log.d("nav_debug", "onBackPressed: $currentFragment ${R.id.todayFragment}")
        if(service_provider_bottom_nav.selectedItemId == R.id.today_navigation && currentFragment == R.id.todayFragment ){
            AlertDialog.Builder(this@ServiceProviderActivity).setMessage("Do you want to exit?")
                .setPositiveButton("Yes"
                ) { p0, p1 -> finishAffinity() }.setNegativeButton("No"
                ) { p0, p1 -> p0?.dismiss() }.create().show()
        }else{
            super.onBackPressed()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val value = super.onSupportNavigateUp()
        Log.d("navigation_debug", "onSupportNavigateUp: $value" )
        return value
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        commonViewModel.processActivityResult(requestCode,resultCode,data)


    }
}