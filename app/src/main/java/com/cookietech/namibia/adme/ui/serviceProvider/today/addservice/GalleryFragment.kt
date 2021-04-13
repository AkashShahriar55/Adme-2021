package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.BuildConfig
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.today.AddServiceViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {
    private var code: Int = 0
    private val PERMISSION_REQUEST_CODE = 1
    private val IMAGE_PICK_CODE_1 = 100
    val IMAGE_PICK_CODE_2 = 101
    val IMAGE_PICK_CODE_3 = 102
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val viewmodel: AddServiceViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("akash_fragment_debug", "onCreate: GalleryFragment")
    }
    init {

        Log.d("akash_fragment_debug", "init: GalleryFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("akash_fragment_debug", "onCreateView: GalleryFragment")
        return inflater.inflate(R.layout.fragment_gallery, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("akash_fragment_debug", "onViewCreated: GalleryFragment")
        if (viewmodel.imageUris[0] != null) {
            Glide.with(requireContext())
                .load(viewmodel.imageUris[0])
                .fitCenter()
                .into(service_image_1)
        }
        if (viewmodel.imageUris[1] != null) {
            Glide.with(requireContext())
                .load(viewmodel.imageUris[1])
                .fitCenter()
                .into(service_image_2)
        }
        if (viewmodel.imageUris[2] != null) {
            Glide.with(requireContext())
                .load(viewmodel.imageUris[2])
                .fitCenter()
                .into(service_image_3)
        }

        initializeFields()
    }

    private fun initializeFields() {
        service_image_1.setOnClickListener { v: View? ->
            code = IMAGE_PICK_CODE_1
            uploadImage()
        }
        service_image_2.setOnClickListener { v: View? ->
            code = IMAGE_PICK_CODE_2
            uploadImage()
        }
        service_image_3.setOnClickListener { v: View? ->
            code = IMAGE_PICK_CODE_3
            uploadImage()
        }
       /* if (isEditing) {
            updateUi()
        }*/
    }

    private fun uploadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Check for Permission
            if (checkPermission()) {
                //Permission is already Granted
                PickImageFromGallery(code)
            } else {
                //permission is not granted
                requestPermission()
            }
        } else {
            //Do  Not Need Permission
            PickImageFromGallery(code)
        }
    }

    private fun PickImageFromGallery(code: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, code)
    }

    private fun requestPermission() {


        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("External Storage Permission")
                .setMessage("External storage permission is must to read your image gallery")
                .setPositiveButton(
                    "Proceed"
                ) { dialog, which -> // No explanation needed, we can request the permission.
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, which -> dialog.dismiss() }.show()
        } else {
            // No explanation needed, we can request the permission.
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    //Handle Request Permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show()
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*** If Storage Permission Is Given, Check External storage is available for read and write */
                PickImageFromGallery(code)
                Toast.makeText(context, "Permission Granted.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    context,
                    "You Should Allow External Storage Permission To Upload image from gallery.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //Handle Image Picker Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE_1) {
                if (BuildConfig.DEBUG && data == null) {
                    error("Assertion failed")
                }
                viewmodel.imageUris[0] = data!!.data
                Glide.with(requireContext())
                    .load(data.data)
                    .fitCenter()
                    .into(service_image_1)
            } else if (requestCode == IMAGE_PICK_CODE_2) {
                if (BuildConfig.DEBUG && data == null) {
                    error("Assertion failed")
                }
                viewmodel.imageUris[1] = data!!.data
                Glide.with(requireContext())
                    .load(data.data)
                    .fitCenter()
                    .into(service_image_2)
            } else if (requestCode == IMAGE_PICK_CODE_3) {
                if (BuildConfig.DEBUG && data == null) {
                    error("Assertion failed")
                }
                viewmodel.imageUris[2] = data!!.data
                Glide.with(requireContext())
                    .load(data.data)
                    .fitCenter()
                    .into(service_image_3)
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GalleryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GalleryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}