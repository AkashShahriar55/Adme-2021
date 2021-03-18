package com.cookietech.namibia.adme.ui.loginRegistration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*


class SplashFragment : Fragment() {
    private var initializationDone: Boolean = false
    var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val loginRegistrationMainViewModel: LoginRegistrationMainViewModel by activityViewModels()

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       val job =  workerScope.launch {
            delay(1000)
            Log.d("akash_view_model_debug", "onViewCreated: " + loginRegistrationMainViewModel.checkIfAlreadyLoggedIn())

            initializationDone = true
           navigateToNextScreen()

        }

        job.invokeOnCompletion {
            Log.d("akash_view_model_debug", "onViewCreated: " + it)
        }
    }

    private fun navigateToNextScreen() {
        val extras = FragmentNavigatorExtras(
            banner_logo to "banner_logo"
        )
        findNavController().navigate(R.id.splash_to_login,null,null,extras)

    }

    override fun onResume() {
        super.onResume()
        if(initializationDone){
            navigateToNextScreen()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                SplashFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}