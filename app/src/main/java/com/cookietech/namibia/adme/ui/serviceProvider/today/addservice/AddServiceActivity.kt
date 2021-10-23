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
import com.cookietech.namibia.adme.managers.FirebaseManager
import com.cookietech.namibia.adme.views.LoadingDialog
import kotlinx.android.synthetic.main.activity_add_service.*
import java.lang.Exception


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
            .setPopUpTo(navController.graph.startDestinationId, false)
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
            onBackPressed()
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

            addServiceViewModel.isServiceUpdate = true
            addServiceViewModel.service = services
            services.mServiceId?.let { addServiceViewModel.getSubServices(it) }

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
        if(currentTab == 2 && hasOnePic > 0 || addServiceViewModel.service.feature_images.isNotEmpty()){
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

        val service_ref = addServiceViewModel.service.mServiceId?:FirebaseManager.mServiceListReference.document().id
        addServiceViewModel.service.mServiceId = service_ref

        if(addServiceViewModel.imageUris.filterNotNull().isNotEmpty()){
            uploadImagesToServer(0)
        }else{
            hideDialog()
            updateToDatabase()
        }


//        addServiceViewModel.imageUris.filterNotNull().let {
//            if(it.isNotEmpty()){
//                Log.d("gallary_debug", "uploadData: started uploading image")
//
//                addServiceViewModel.uploadImagesToServer(service_ref+"_image1",it[0],object : AddServiceRepository.UploadImageCallback{
//                    override fun onProgressUpdate(mb: String) {
//                        updateProgressUi(mb)
//                    }
//
//                    override fun onUploadFailed(exception: Exception?) {
//                        Log.d("gallary_debug", "uploadData: uploading image 0 "+ exception?.message)
//                        hideDialog()
//                    }
//
//                    override fun onUploadSuccessful(url: String) {
//                        imageUrls.add(url)
//                        hideDialog()
//                        if(it.size>1){
//                            showDialog("Uploading image 2/$imagecount","please wait...")
//                            addServiceViewModel.uploadImagesToServer(service_ref+"_image2",it[1],object : AddServiceRepository.UploadImageCallback{
//                                override fun onProgressUpdate(mb: String) {
//                                    updateProgressUi(mb)
//                                }
//
//                                override fun onUploadFailed(exception: Exception?) {
//                                    hideDialog()
//                                    Log.d("gallary_debug", "uploadData: uploading image 1 "+ exception?.message)
//                                }
//
//                                override fun onUploadSuccessful(url: String) {
//                                    imageUrls.add(url)
//                                    hideDialog()
//                                    if(it.size>2){
//                                        showDialog("Uploading image 2/$imagecount","please wait...")
//                                        addServiceViewModel.uploadImagesToServer(service_ref+"_image3",it[2],object : AddServiceRepository.UploadImageCallback{
//                                            override fun onProgressUpdate(mb: String) {
//                                                updateProgressUi(mb)
//                                            }
//
//                                            override fun onUploadFailed(exception: Exception?) {
//                                                hideDialog()
//                                                Log.d("gallary_debug", "uploadData: uploading image 3 "+ exception?.message)
//                                            }
//
//                                            override fun onUploadSuccessful(url: String) {
//                                                imageUrls.add(url)
//                                                hideDialog()
//                                                updateToDatabase(imageUrls)
//                                            }
//
//                                        })
//                                    }else{
//                                        updateToDatabase(imageUrls)
//                                    }
//                                }
//
//                            })
//                        }else{
//                            updateToDatabase(imageUrls)
//                        }
//                    }
//
//                })
//            }else{
//
//                hideDialog()
//            }

//        }
    }

    private fun uploadImagesToServer(imageNo: Int) {



        val imageName = addServiceViewModel.service.mServiceId!! + "_image"+(imageNo+1);
        val imageHasInServer =
            addServiceViewModel.service.feature_images.any { it.contains(imageName, true) }
        val imageUri = addServiceViewModel.imageUris[imageNo]


        if(imageUri != null ){
            addServiceViewModel.uploadImagesToServer(imageName,imageUri,object : AddServiceRepository.UploadImageCallback{
                override fun onProgressUpdate(mb: String) {
                    updateProgressUi(mb)
                }

                override fun onUploadFailed(exception: Exception?) {
                    hideDialog()
                    Log.d("gallary_debug", "uploadData: uploading image 3 "+ exception?.message)
                    Toast.makeText(this@AddServiceActivity,"Something went wrong",Toast.LENGTH_LONG).show()
                }

                override fun onUploadSuccessful(url: String) {
                    if(!imageHasInServer){
                        addServiceViewModel.service.feature_images.add(url)
                    }else{
                        val lastUrl =
                            addServiceViewModel.service.feature_images.find { it.contains(imageName) }
                        val index = addServiceViewModel.service.feature_images.indexOf(lastUrl);
                        addServiceViewModel.service.feature_images[index] = url
                    }

                    if(imageNo != 2){
                        uploadImagesToServer(imageNo+1)
                    }else{
                        hideDialog()
                        updateToDatabase()
                    }

                }

            })
        }else if(imageNo !=2){
            uploadImagesToServer(imageNo+1)
        }else{
            hideDialog()
            updateToDatabase()
        }




    }

    private fun updateToDatabase() {
        showDialog("Creating service","Please wait...")
        addServiceViewModel.updateDatabase()?.addOnCompleteListener {task->
            if(task.isSuccessful){

                addServiceViewModel.updateSubServices(addServiceViewModel.service.mServiceId!!)?.addOnSuccessListener {
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