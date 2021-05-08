package com.cookietech.namibia.adme.ui.invoice

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.appointment.AppointmentRepository
import com.cookietech.namibia.adme.architecture.appointment.AppointmentViewModel
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.cookietech.namibia.adme.models.SubServicesPOJO
import com.cookietech.namibia.adme.utils.UiHelper
import com.cookietech.namibia.adme.views.LoadingDialog
import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.fragment_invoice.*
import kotlinx.android.synthetic.main.otp_bottom_sheet_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class InvoiceFragment : Fragment() {
    private lateinit var dialog: LoadingDialog
    private var total: Float = 0.0f
    private var adapter: ServiceDetailsAdapter? = null
    private var appointment: AppointmentPOJO? = null
    val viewModel: AppointmentViewModel by activityViewModels()
    var services = ArrayList<SubServicesPOJO>()
    var isEditable = true
    var discountAmount = 0.0f

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
        return inflater.inflate(R.layout.fragment_invoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.invoice_menu)
        dialog = LoadingDialog(requireContext(), "none", "none")
        initializeObservers()
        initializeRecyclerView()


        toolbar.getChildAt(1).setOnClickListener {
            if(isEditable){
                showAlert()
            }else{
                findNavController().navigateUp()
            }
        }


        if(isEditable){
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        toolbar.menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                        toolbar.menu.getItem(0).isVisible = false
                        toolbar.menu.getItem(1).isVisible = true
                        toolbar.menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                        EnableEdit()
                        btn_send_invoice.visibility = View.GONE

                        et_discount_amount.setText("$discountAmount")
                        true
                    }
                    R.id.menu_done -> {
                        toolbar.menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                        toolbar.menu.getItem(1).isVisible = false
                        toolbar.menu.getItem(0).isVisible = true
                        toolbar.menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                        DisableEdit()
                        btn_send_invoice.visibility = View.VISIBLE
                        updateIncome()
                        true
                    }
                    R.id.meu_download->{
                        generatePdf()
                        true
                    }
                    else -> {
                        super.onOptionsItemSelected(it)
                    }
                }
            }
        }else{
            toolbar.menu.getItem(0).isVisible = false
            toolbar.menu.getItem(1).isVisible = false
            btn_send_invoice.visibility = View.GONE
        }

        btn_send_invoice.setOnClickListener {
            sendInvoiceAndFinish()
        }
    }

    private fun sendInvoiceAndFinish() {
        showDialog("Sending Invoice","Please wait...")
        val totalHeight = root_scrollView.getChildAt(0).height
        val totalWidth = root_scrollView.getChildAt(0).width
        var bitmap = getBitmapFromView(root_scrollView, totalHeight, totalWidth)
        bitmap?.let { invoice->
            viewModel.uploadInvoiceToStorage("invoice_${appointment?.id}.jpg",invoice,object :AppointmentRepository.UploadInvoiceCallback{
                override fun onProgressUpdate(mb: String) {
                    updateProgressUi(mb)
                }

                override fun onUploadFailed(exception: Exception?) {
                    hideDialog()
                }

                override fun onUploadSuccessful(url: String) {
                    appointment?.apply {
                        updateProgressUi("Please wait...")
//                        invoice_link = url
//                        completed = true
                        viewModel.sendInvoiceAndFinish(this).addOnSuccessListener {
                            hideDialog()
                        }.addOnFailureListener {
                            hideDialog()
                        }
                    }

                }

            })
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

    private fun updateIncome() {
        discountAmount = et_discount_amount.text?.toString()?.toFloat()?:0.0f
        et_discount_amount.setText("-$ $discountAmount")
        val discountTotal = total - discountAmount
        total_amount.text =  "$ $discountTotal"
        invoice_total.text =  "$ $discountTotal"
    }

    private fun DisableEdit() {
        et_discount_amount.isEnabled = false
        et_discount_amount.setBackgroundResource(0)
    }

    private fun EnableEdit() {
        et_discount_amount.isEnabled = true
        et_discount_amount.background = getDrawable(requireContext(), R.drawable.bottom_border_grey)
    }

    private fun initializeRecyclerView() {
        service_details_rv.layoutManager = LinearLayoutManager(requireContext())
        service_details_rv.setHasFixedSize(true)
        adapter = ServiceDetailsAdapter()
        service_details_rv.adapter = adapter
    }

    private fun initializeObservers() {
        viewModel.observableAppointment.observe(viewLifecycleOwner, {
            it?.let { value ->
                appointment = value
                updateBasicInfo()
            }
        })

        viewModel.observableFinalServices.observe(viewLifecycleOwner, {
            it?.let { value ->
                services = value
                updateServicesInfo()
            }
        })
    }

    private fun updateServicesInfo() {
        adapter?.let { it.servicesList = services }
        for (service in services){
            val cost = (service.service_charge?.toFloat()?:0.0f) * service.quantity
            total+=cost
        }

        subtotal.text = "$ $total"

        txt_vat.text = "Vat(${AppComponent.vat}%)"
        val vat = (total * AppComponent.vat)/100
        total_vat.text = "+$ $vat"

        total += vat

        total_amount.text = "$ $total"
        invoice_total.text = "$ $total"

        issue_date.text = UiHelper.getDate(System.currentTimeMillis(), "dd MMM yyyy")
    }

    private fun updateBasicInfo() {
        customer_name.setText(appointment?.client_name)
        customer_address.setText(appointment?.client_address)
        customer_phone.setText(appointment?.client_phone)
        service_provider.text = appointment?.service_provider_name
        service_category.text = appointment?.service_name
    }


    private fun showAlert() {
        val exitAlert = AlertDialog.Builder(requireContext())
        exitAlert.setTitle("Are you sure?")
            .setMessage("Do you want to exit? It will delete all your saved progress")
            .setPositiveButton(
                "Exit"
            ) { dialog, which -> findNavController().navigateUp()}
            .setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.dismiss() }
        exitAlert.show()
    }

    private fun generatePdf() {
        val totalHeight = root_scrollView.getChildAt(0).height
        val totalWidth = root_scrollView.getChildAt(0).width
        val btn_height = btn_send_invoice.height
        var goneForBitmap = false
        if(btn_send_invoice.visibility == View.VISIBLE){
            btn_send_invoice.visibility = View.GONE
            goneForBitmap =true
        }

        var bitmap = getBitmapFromView(root_scrollView, totalHeight, totalWidth)
        if(goneForBitmap){
            btn_send_invoice.visibility = View.VISIBLE
        }
        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = bitmap?.height ?: 0
        val width = bitmap?.width ?: 0
        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, width, height, true)
        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)
        val file: File = File(requireContext().filesDir, "invoice_${appointment?.id}.pdf")
        try {
            document.writeTo(FileOutputStream(file))
            document.close()
            file.setReadable(true, false)
            SharePdf(file)
            btn_send_invoice.visibility = View.VISIBLE
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun SharePdf(file: File) {
        val fileUri = FileProvider.getUriForFile(
            requireContext(),
            getApplicationContext().packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//        intent.putExtra(Intent.EXTRA_EMAIL, receiver_email)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Invoice for Service")
        intent.type = "application/pdf"
        //need to fix the bug if no shareable app found
        startActivity(Intent.createChooser(intent, "Share Invoice Via"))
    }

    private fun getBitmapFromView(view: View, totalHeight: Int, totalWidth: Int): Bitmap? {
        val returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            InvoiceFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}