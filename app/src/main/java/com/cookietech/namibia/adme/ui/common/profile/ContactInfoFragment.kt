package com.cookietech.namibia.adme.ui.common.profile

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.databinding.FragmentContactInfoBinding
import com.cookietech.namibia.adme.managers.FirebaseManager
import kotlinx.android.synthetic.main.fragment_profile.*


class ContactInfoFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentContactInfoBinding? = null
    private val binding get() = _binding!!

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
        binding.backButton.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeFields() {
        setProfilePhoto()
        setUserName()
        setphoneNumber()

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

    companion object {

        @JvmStatic
        fun newInstance() =
            ContactInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}