package com.cookietech.namibia.adme.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.cookietech.namibia.adme.R
import kotlinx.android.synthetic.main.custom_progress_dialog.*

class LoadingDialog(context: Context, private val title: String, private val message: String) :
    Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_progress_dialog)
        setCancelable(false)
        custom_dialog_title.text = title
        tv_progress.text = message
    }

    override fun onStart() {
        super.onStart()
    }

    fun updateMessage(message: String) {
        tv_progress.text = message
    }

    fun updateTitle(title: String) {
        custom_dialog_title.text = title
    }
}