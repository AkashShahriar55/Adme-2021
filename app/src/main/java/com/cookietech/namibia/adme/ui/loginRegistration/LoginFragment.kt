package com.cookietech.namibia.adme.ui.loginRegistration
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel
import com.cookietech.namibia.adme.managers.ConnectionManager
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.views.CustomToast
import com.cookietech.namibia.adme.views.LoadingDialog
import java.lang.Exception


class LoginFragment : Fragment() {

    val loginViewModel : LoginViewModel by viewModels()
    val mainViewModel : LoginRegistrationMainViewModel by activityViewModels()
    private lateinit var dialog: LoadingDialog
    init {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariables()
        initializeClicks()

        ConnectionManager.networkAvailability.observe(viewLifecycleOwner,{
            if(it == false){
              showNetworkErrorMessage()
            }else{

            }
        })
    }

    fun showNetworkErrorMessage(){
        CustomToast.makeErrorToast(requireContext(),"No internet! Please check your internet connection",Toast.LENGTH_LONG).show()
    }

    private fun initializeVariables() {
        /**Get Google client**/
        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        dialog = context?.let { LoadingDialog(it, "Logging in", "Please wait...") }!!
        val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.login_slide_animation)
        login_container.startAnimation(hyperspaceJumpAnimation)
        login_phone_btn.setOnClickListener {
            findNavController().navigate(R.id.login_to_registration)
        }

        mainViewModel.addActivityCallback(object : LoginRegistrationMainViewModel.ActivityCallbacks{
            override fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                dialog.dismiss()
                loginViewModel.processActivityResult(requestCode,resultCode,data)
            }

        })

        loginViewModel.loginCallback = object :LoginViewModel.LoginCallback{
            override fun onLoginSuccessful() {
                dialog.show()
                val login = mainViewModel.tryToLogin(object : LoginAndRegistrationManager.UserCreationCallback {
                    override fun onUserCreationSuccessful() {
                        Log.d("login_debug", "onUserCreationSuccessful: ")
                        mainViewModel.updateFCMToken()
                        dialog.dismiss()
                        findNavController().navigate(R.id.login_to_user_info)
                    }

                    override fun onUserFetchSuccessful() {
                        Log.d("login_debug", "onUserFetchSuccessful: ")
                        mainViewModel.updateFCMToken()
                        dialog.dismiss()
                        when (SharedPreferenceManager.user_mode) {
                            AppComponent.MODE_CLIENT -> findNavController().navigate(R.id.login_to_client_activity)
                            AppComponent.MODE_SERVICE_PROVIDER -> findNavController().navigate(R.id.login_to_service_activity)
                        }
                    }

                    override fun onUserCreationFailed(exception: Exception) {
                        dialog.dismiss()
                        Toast.makeText(context,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                        Log.d("login_debug", "onUserCreationFailed: ")
                    }

                })

                Log.d("login_debug", "onLoginSuccessful:$login ")
            }

            override fun onLoginFailed() {
                dialog.dismiss()
                Toast.makeText(context,"Something Went Wrong!",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initializeClicks() {
        /**Google Login**/
        login_google_btn.setOnClickListener{
            //Toast.makeText(requireContext(),"Clicked",Toast.LENGTH_SHORT).show()
            if(ConnectionManager.isOnline(requireContext()))
                signInWithGoogle()
            else
                showNetworkErrorMessage()

        }

        /**Facebook Login**/
        login_facebook_btn.setOnClickListener{
            if(ConnectionManager.isOnline(requireContext()))
                signInWithFacebook()
            else
                showNetworkErrorMessage()

        }


    }

    private fun signInWithFacebook() {

        Log.d("fb_login_debug", "signInWithFacebook: Called")
        dialog.show()
        loginViewModel.signInWithFacebook(requireActivity())
    }



    private fun signInWithGoogle() {
        dialog.show()
        loginViewModel.signInWithGoogle(requireActivity())

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}