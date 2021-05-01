package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.today.AddServiceRepository
import com.cookietech.namibia.adme.architecture.serviceProvider.today.AddServiceViewModel
import com.cookietech.namibia.adme.views.LoadingDialog
import kotlinx.android.synthetic.main.activity_add_service.*
import kotlinx.android.synthetic.main.fragment_overview.*
import java.lang.Exception
import java.util.*


class AddServiceActivity : AppCompatActivity() {
    private lateinit var dialog: LoadingDialog
    private lateinit var navOptions: NavOptions
    private lateinit var navController: NavController
    val addServiceViewModel: AddServiceViewModel by viewModels()
    var currentTab = 0
    val args: AddServiceActivityArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)
        setUpServicesForUpdate()
        dialog = LoadingDialog(this, "none", "none")
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.add_service_nav_host) as NavHostFragment
        navController = navHostFragment.findNavController()
        navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()

        add_service_overview_text.setOnClickListener {
            navController.navigate(R.id.overviewFragment, null, navOptions);
            toggleTab(0)
            currentTab = 0
        }

        add_service_services_text.setOnClickListener {
            navController.navigate(R.id.servicesFragment, null, navOptions);
            toggleTab(1)
            currentTab = 1
        }

        add_service_gallery_text.setOnClickListener {
            navController.navigate(R.id.galleryFragment, null, navOptions);
            toggleTab(2)
            currentTab = 2
        }

        add_service_cancel_button.setOnClickListener {
            it.findNavController().navigateUp()
        }

        add_service_save_button.setOnClickListener {
            Log.d("save_debug", "verifyAndSave: clicked")
            verifyAndSave()
        }

        //val num = args.number
        //Log.d("arg_debug", "onCreate: $num")

    }

    private fun setUpServicesForUpdate() {
        val services = args.services
        if (services == null){
            Log.d("arg_debug", "null service: ")
            addServiceViewModel.isServiceUpdate = false

        }
        else{
            Log.d("arg_debug", "ready for update: ${services.mServiceId}")
            Log.d("arg_debug", "ready for update: ${services.startTime}")
            Log.d("arg_debug", "ready for update: ${services.endTime}")
            addServiceViewModel.isServiceUpdate = true
            addServiceViewModel.service = services

        }
    }


    fun toggleTab(index: Int){
        if(addServiceViewModel.completedFlags[0]){
            add_service_1_image.setImageResource(R.drawable.ic_1_green)
            add_service_overview_text.setTextColor(resources.getColor(R.color.material_green))
        }else{
            add_service_1_image.setImageResource(R.drawable.ic_1_gray)
            add_service_overview_text.setTextColor(resources.getColor(R.color.add_service_step_txt_gray))
        }
        if(addServiceViewModel.completedFlags[1]){
            add_service_2_image.setImageResource(R.drawable.ic_2_green)
            add_service_services_text.setTextColor(resources.getColor(R.color.material_green))
        }else{
            add_service_2_image.setImageResource(R.drawable.ic_2_gray)
            add_service_services_text.setTextColor(resources.getColor(R.color.add_service_step_txt_gray))
        }

        if(addServiceViewModel.completedFlags[2]){
            add_service_3_image.setImageResource(R.drawable.ic_3_green)
            add_service_gallery_text.setTextColor(resources.getColor(R.color.material_green))
        }else{
            add_service_3_image.setImageResource(R.drawable.ic_3_gray)
            add_service_gallery_text.setTextColor(resources.getColor(R.color.add_service_step_txt_gray))
        }
        when (index) {
            0 -> {
                add_service_1_image.setImageResource(R.drawable.ic_1_blue)
                add_service_overview_text.setTextColor(resources.getColor(R.color.txt_highlight))
                add_service_save_button.text = "Save and Continue"
            }
            1 -> {
                add_service_2_image.setImageResource(R.drawable.ic_2_blue)
                add_service_services_text.setTextColor(resources.getColor(R.color.txt_highlight))
                add_service_save_button.text = "Save and Continue"
            }
            else -> {
                add_service_3_image.setImageResource(R.drawable.ic_3_blue)
                add_service_gallery_text.setTextColor(resources.getColor(R.color.txt_highlight))
                add_service_save_button.text = "Save and Finish"
            }
        }
    }

    private fun verifyAndSave() {
        Log.d("save_debug", "verifyAndSave: " + addServiceViewModel.service.toString())
        if(currentTab == 0 && !addServiceViewModel.service.category.isNullOrEmpty()
            && !addServiceViewModel.service.categoryId.isNullOrEmpty()
            && !addServiceViewModel.service.description.isNullOrEmpty()
            && !addServiceViewModel.service.startTime.isNullOrEmpty()
            && !addServiceViewModel.service.endTime.isNullOrEmpty() && addServiceViewModel.service.description!!.length > 40){
            addServiceViewModel.completedFlags[0] = true

            Log.d("save_debug", "verifyAndSave: true")
        }else if(currentTab == 0){
            Toast.makeText(this, "Please fill everything properly and save", Toast.LENGTH_SHORT).show()
            return
        }

        if(currentTab == 1 && addServiceViewModel.subServicesLiveData.value != null){
            if(addServiceViewModel.subServicesLiveData.value!!.size >= 0){
                addServiceViewModel.completedFlags[1] = true
            }
        }else if(currentTab == 1){
            Toast.makeText(this, "Please add at least 1 service and save", Toast.LENGTH_SHORT).show()
            return
        }


        val hasOnePic = addServiceViewModel.imageUris.count { it!=null }
        if(currentTab == 2 && hasOnePic > 0){
            addServiceViewModel.completedFlags[2] = true
        }else if(currentTab == 2){
            Toast.makeText(this, "Please add at least 1 images and save", Toast.LENGTH_SHORT).show()
            return
        }


        if(currentTab!=0 && !addServiceViewModel.completedFlags[0]){
            navController.navigate(R.id.overviewFragment, null, navOptions);
            toggleTab(0)
            currentTab = 0
            Toast.makeText(this, "Please fill everything properly and save", Toast.LENGTH_SHORT).show()
        }else if(currentTab != 1 && !addServiceViewModel.completedFlags[1]){
            navController.navigate(R.id.servicesFragment, null, navOptions);
            toggleTab(1)
            currentTab = 1
            Toast.makeText(this, "Please add at least 1 service and save", Toast.LENGTH_SHORT).show()
        }else if(currentTab != 2 && !addServiceViewModel.completedFlags[2]){
            navController.navigate(R.id.galleryFragment, null, navOptions);
            toggleTab(2)
            currentTab = 2
            Toast.makeText(this, "Please add at least 1 images and save", Toast.LENGTH_SHORT).show()
        }else if(currentTab != 2){
            navController.navigate(R.id.galleryFragment, null, navOptions);
            toggleTab(2)
            currentTab = 2
        }else{
            Log.d("upload_debug", "verifyAndSave: ")
            uploadData()
        }
    }

    private fun uploadData() {
        val imageUrls = mutableListOf<String>()
        val imagecount = addServiceViewModel.imageUris.count { it!=null }
        showDialog("Uploading image 1/$imagecount","please wait...")

        addServiceViewModel.imageUris.filterNotNull().let {
            if(it.isNotEmpty()){

                addServiceViewModel.uploadImagesToServer(it[0],object : AddServiceRepository.UploadImageCallback{
                    override fun onProgressUpdate(mb: String) {
                        updateProgressUi(mb)
                    }

                    override fun onUploadFailed(exception: Exception?) {
                        hideDialog()
                    }

                    override fun onUploadSuccessful(url: String) {
                        imageUrls.add(url)
                        hideDialog()
                        if(it.size>1){
                            showDialog("Uploading image 2/$imagecount","please wait...")
                            addServiceViewModel.uploadImagesToServer(it[1],object : AddServiceRepository.UploadImageCallback{
                                override fun onProgressUpdate(mb: String) {
                                    updateProgressUi(mb)
                                }

                                override fun onUploadFailed(exception: Exception?) {
                                    hideDialog()
                                }

                                override fun onUploadSuccessful(url: String) {
                                    imageUrls.add(url)
                                    hideDialog()
                                    if(it.size>2){
                                        showDialog("Uploading image 2/$imagecount","please wait...")
                                        addServiceViewModel.uploadImagesToServer(it[2],object : AddServiceRepository.UploadImageCallback{
                                            override fun onProgressUpdate(mb: String) {
                                                updateProgressUi(mb)
                                            }

                                            override fun onUploadFailed(exception: Exception?) {
                                                hideDialog()
                                            }

                                            override fun onUploadSuccessful(url: String) {
                                                imageUrls.add(url)
                                                hideDialog()
                                                updateToDatabase(imageUrls)
                                            }

                                        })
                                    }else{
                                        updateToDatabase(imageUrls)
                                    }
                                }

                            })
                        }else{
                            updateToDatabase(imageUrls)
                        }
                    }

                })
            }else{
                hideDialog()
            }

        }
    }

    private fun updateToDatabase(imageUrls: List<String>) {
        showDialog("Creating service","Please wait...")
        addServiceViewModel.service.feature_images = imageUrls
        addServiceViewModel.updateDatabase()?.addOnCompleteListener {task->
            if(task.isSuccessful){

                addServiceViewModel.updateSubServices(task.result.id)?.addOnSuccessListener {
                    hideDialog()
                    finish()
                }?.addOnFailureListener {
                    hideDialog()
                } ?: kotlin.run {
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT)
                }
            }else{
                hideDialog()
            }

        }?.addOnFailureListener {
            hideDialog()
        }
    }

    private fun updateProgressUi(mb: String) {
        if(dialog.isShowing){
            dialog.updateMessage("Uploading $mb mb")
        }
    }

    fun showDialog(title:String,message:String){
        dialog.show()
        dialog.updateTitle(title)
        dialog.updateMessage(message)
    }


    fun hideDialog(){
        if(dialog.isShowing){
            dialog.dismiss()
        }

    }

    override fun onNavigateUp(): Boolean {
        val value = super.onNavigateUp()
        Log.d("add_service_debug", "onNavigateUp: $value")
        return value

    }

    override fun onBackPressed() {
        Log.d("add_service_debug", "onBackPressed: ")
        toggleTab(0)
        super.onBackPressed()
    }
}