package com.cookietech.namibia.adme.ui.common.profile

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.common.CommonViewModel
import com.cookietech.namibia.adme.architecture.common.profile.ContactInfoViewModel
import com.cookietech.namibia.adme.databinding.FragmentContactInfoBinding
import com.cookietech.namibia.adme.interfaces.AuthConnectionCallback
import com.cookietech.namibia.adme.interfaces.ImageUploadCallback
import com.cookietech.namibia.adme.managers.ConnectionManager
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.views.CustomToast
import com.cookietech.namibia.adme.views.LoadingDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_user_info.*


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

    /** For Image Picker**/
    private var imageLauncher : ActivityResultLauncher<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    /**Lifecycle Method**/
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

        binding.profileImageContact.setOnClickListener {
            openPicker()
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

        /**updae info**/
        binding.updateInfoBtn.setOnClickListener {
            updateInfo()
        }
    }

    private fun updateInfo() {
        commonViewModel.imageUri?.apply {
            commonViewModel.uploadImage(this,object : ImageUploadCallback {
                override fun onImageUploaded(url: String) {
                    Log.d("update_debug", "onImageUploaded ")
                    commonViewModel.downloadImageUrl = url
                    updateUserData()
                }

                override fun onImageUploadFailed() {
                    Log.d("update_debug", "onImageUploadFailed ")
                    Toast.makeText(requireContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

            })
        } ?: kotlin.run {
            Log.d("update_debug", "imageUri null: ")
            updateUserData()
        }
    }

    private fun updateUserData() {
        commonViewModel.updateUserData(commonViewModel.userNme,commonViewModel.downloadImageUrl)

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
        imageLauncher = null
    }

    private fun initializeFields() {
        dialog = context?.let { LoadingDialog(it, "Linking", "Please wait...") }!!
        setProfilePhoto()
        setUserName()
        setphoneNumber()
        setConnectedAuthProvider()
        initializeCallbacks()
        addTextChangedListener()
        registerActivityLauncher()

    }

    private fun registerActivityLauncher() {
        imageLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            commonViewModel.imageUri = uri
            commonViewModel.imageUri?.let {
                Glide.with(this)
                    .load(it)
                    .fitCenter()
                    .into(binding.profileImageContact)
                binding.updateInfoBtn.visibility = View.VISIBLE
            }


        }
    }

    private fun addTextChangedListener() {
        binding.edtUserName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableUpdateButtonForUserNameChanged(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun enableUpdateButtonForUserNameChanged(p0: CharSequence?) {
        p0?.let {
            if(it.toString().trim() == FirebaseManager.currentUser?.user_name || it.length<=0){
                binding.updateInfoBtn.visibility = View.GONE
                commonViewModel.userNme = null
            } else{
                binding.updateInfoBtn.visibility = View.VISIBLE
                commonViewModel.userNme = it.toString().trim()
            }
        }
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
        FirebaseManager.currentUser?.profile_image_url?.let {
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
        binding.edtUserName.setText((FirebaseManager.currentUser?.user_name))
    }

    fun showNetworkErrorMessage(){
        CustomToast.makeErrorToast(requireContext(),"No internet! Please check your internet connection",Toast.LENGTH_LONG).show()
    }
    fun showDuplicatedCredentialError(){

        CustomToast.makeErrorToast(requireContext(),"This credential is already associated with a different user account",Toast.LENGTH_LONG).show()
    }

    private fun openPicker() {
        Log.d("permission_debug", "openPicker: ")
        Dexter.withContext(context)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    imageLauncher?.launch("image/*")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.d("permission_debug", "onPermissionDenied: ")
                    /*TODO("Not yet implemented")*/
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    /* TODO("Not yet implemented")*/
                    Log.d("permission_debug", "onPermissionRationaleShouldBeShown: ")
                    AlertDialog.Builder(context).setTitle("We need this permission!")
                        .setMessage("External storage permission is must to read your image gallery")
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { dialog, which ->
                            dialog.dismiss()
                            token?.cancelPermissionRequest()
                        }
                        .setPositiveButton(android.R.string.ok
                        ) { dialog, which ->
                            dialog.dismiss()
                            token?.continuePermissionRequest()
                        }
                        .setOnDismissListener(DialogInterface.OnDismissListener { token?.cancelPermissionRequest() })
                        .show()
                }

            }).check()
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