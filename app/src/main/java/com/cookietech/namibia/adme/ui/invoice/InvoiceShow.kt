package com.cookietech.namibia.adme.ui.invoice

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.appointment.AppointmentViewModel
import com.cookietech.namibia.adme.models.AppointmentPOJO
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.fragment_invoice.*
import kotlinx.android.synthetic.main.fragment_invoice_show.*
import kotlinx.android.synthetic.main.fragment_invoice_show.invoice_layout
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class InvoiceShow : Fragment() {
    private var appointment: AppointmentPOJO? = null
    val viewModel: AppointmentViewModel by activityViewModels()
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
        return inflater.inflate(R.layout.fragment_invoice_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeObservers()
    }

    private fun initializeObservers() {
        viewModel.observableAppointment.observe(viewLifecycleOwner) {
            it?.let { value ->
                appointment = value
                updateUi()

            }
        }
    }



    private fun updateUi() {
        appointment?.invoice_link?.let { link->
            context?.let {
                Glide.with(it)
                    .load(link)
                    .listener(object :RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            invoice_progress.visibility= View.GONE
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            invoice_progress.visibility= View.GONE
                            setUpDownloadToolbar()
                            return false
                        }

                    })
                    .into(invoice_image)
            }
        }
    }

    private fun setUpDownloadToolbar() {
        toolbar_show.inflateMenu(R.menu.invoice_menu)
        toolbar_show.menu.getItem(0).isVisible = false
        toolbar_show.menu.getItem(1).isVisible = false
        toolbar_show.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.meu_download->{
                    generatePdf()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun generatePdf() {
        val totalHeight = invoice_layout.height
        val totalWidth = invoice_layout.width
        var bitmap = getBitmapFromView(invoice_layout, totalHeight, totalWidth)
        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = bitmap?.height ?: 0
        val width = bitmap?.width ?: 0
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
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
            FacebookSdk.getApplicationContext().packageName + ".provider",
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InvoiceShow.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            InvoiceShow().apply {
                arguments = Bundle().apply {

                }
            }
    }
}