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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel


class LoginFragment : Fragment() {

    val loginViewModel : LoginViewModel by viewModels()
    val mainViewModel : LoginRegistrationMainViewModel by activityViewModels()

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
    }

    private fun initializeVariables() {
        /**Get Google client**/
        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.login_slide_animation)
        login_container.startAnimation(hyperspaceJumpAnimation)
        login_phone_btn.setOnClickListener {
            findNavController().navigate(R.id.login_to_registration)
        }

        mainViewModel.addActivityCallback(object : LoginRegistrationMainViewModel.ActivityCallbacks{
            override fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                loginViewModel.processActivityResult(requestCode,resultCode,data)
            }

        })
    }

    private fun initializeClicks() {
        /**Google Login**/
        login_google_btn.setOnClickListener{
            //Toast.makeText(requireContext(),"Clicked",Toast.LENGTH_SHORT).show()
            signInWithGoogle()
        }

        /**Facebook Login**/
        login_facebook_btn.setOnClickListener{
            signInWithFacebook()
        }


    }

    private fun signInWithFacebook() {

        Log.d("fb_login_debug", "signInWithFacebook: Called")

        loginViewModel.signInWithFacebook(requireActivity())
    }



    private fun signInWithGoogle() {
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