package com.cookietech.namibia.adme.ui.common.profile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.CommonViewModel
import com.cookietech.namibia.adme.architecture.common.profile.ContactInfoViewModel
import com.cookietech.namibia.adme.databinding.FragmentContactInfoBinding
import com.cookietech.namibia.adme.interfaces.AuthConnectionCallback
import com.cookietech.namibia.adme.managers.ConnectionManager
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.views.CustomToast
import com.cookietech.namibia.adme.views.LoadingDialog
import kotlinx.android.synthetic.main.fragment_profile.*


class ContactInfoFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentContactInfoBinding? = null
    private val viewModel : ContactInfoViewModel by viewModels()
    private val commonViewModel : CommonViewModel by activityViewModels()
    private val binding get() = _binding!!
    private var isConnectedWithGoogle = false
    private var isConnectedWithFacebook = false
    private lateinit var dialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        /**back button click**/
        binding.backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        /**connect with google**/
        binding.connectGoogleBtn.setOnClickListener {
            if(!ConnectionManager.isOnline(requireContext())){
                showNetworkErrorMessage()
                return@setOnClickListener
            }
            else if (isConnectedWithGoogle){
                Toast.makeText(requireContext(),"Already Connected",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else{
                connectWithGoogle()
            }

        }

        /**connect with facebook**/
        binding.connectFacebookBtn.setOnClickListener {
            if(!ConnectionManager.isOnline(requireContext())){
                showNetworkErrorMessage()
                return@setOnClickListener
            }
            else if (isConnectedWithFacebook){
                Toast.makeText(requireContext(),"Already Connected",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else{
                connectwithFacebook()
            }
        }
    }

    private fun connectwithFacebook() {
        dialog.show()
        viewModel.signInWithFacebook(requireActivity())

    }

    private fun connectWithGoogle() {
        dialog.show()
        viewModel.signInWithGoogle(requireActivity())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeFields() {
        dialog = context?.let { LoadingDialog(it, "Linking", "Please wait...") }!!
        setProfilePhoto()
        setUserName()
        setphoneNumber()
        setConnectedAuthProvider()
        initializeCallbacks()

    }

    private fun initializeCallbacks() {
        viewModel.authConnectionCallback = object : AuthConnectionCallback{
            override fun onAuthConnectionSuccessful(provider: String) {
                dialog.dismiss()
                if (provider.equals("facebook")){
                    binding.connectFacebookBtn.setText(R.string.connected)
                } else if (provider.equals("google")){
                    binding.connectGoogleBtn.setText(R.string.connected)
                }

            }

            override fun onAuthConnectionFailed() {
                dialog.dismiss()
                showDuplicatedCredentialError()

            }


        }

        commonViewModel.addCommonActivityCallback(object : CommonViewModel.CommonActivityCallbacks{
            override fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                //dialog.dismiss()
                viewModel.processActivityResult(requestCode,resultCode,data)
            }

        })


    }

    private fun setConnectedAuthProvider() {
        //val connectedAuthProvider =
        FirebaseManager.getConnectedAuthProvider().let {
            isConnectedWithFacebook = it["facebook"]!!
            isConnectedWithGoogle = it["google"]!!
        }


        if (isConnectedWithGoogle){
            binding.connectGoogleBtn.setText(R.string.connected)

        } else {
            binding.connectGoogleBtn.setText(R.string.connect)
        }

        if (isConnectedWithFacebook){
            binding.connectFacebookBtn.setText(R.string.connected)
        } else {
            binding.connectFacebookBtn.setText(R.string.connect)
        }
    }

    private fun setphoneNumber() {
        binding.edtPhoneNumber.setText((FirebaseManager.currentUser?.phone ?: ""))
    }

    private fun setProfilePhoto() {
        Log.d("pro_pic_debug", "setProfilePhoto: " + FirebaseManager.mFirebaseUser!!.photoUrl)
        FirebaseManager.currentUser!!.profile_image_url?.let {
            Glide.with(requireContext())
                .load(it)
                .placeholder(R.mipmap.default_user_photo)
                .into(binding.profileImageContact)
        } ?: kotlin.run {

            Glide.with(requireContext())
                .load(FirebaseManager.mFirebaseUser!!.photoUrl)
                .placeholder(R.mipmap.default_user_photo)
                .into(binding.profileImageContact)
        }

    }
    private fun setUserName() {
        binding.edtUserName.setText((FirebaseManager.currentUser?.user_name ?: "Adme User"))
    }

    fun showNetworkErrorMessage(){
        CustomToast.makeErrorToast(requireContext(),"No internet! Please check your internet connection",Toast.LENGTH_LONG).show()
    }
    fun showDuplicatedCredentialError(){

        CustomToast.makeErrorToast(requireContext(),"This credential is already associated with a different user account",Toast.LENGTH_LONG).show()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ContactInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}