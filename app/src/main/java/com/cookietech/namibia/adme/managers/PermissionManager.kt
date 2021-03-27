package com.cookietech.namibia.adme.managers

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import com.cookietech.namibia.adme.Application.AdmeApplication
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object PermissionManager {

    var workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    fun checkLocationPermission(
        context: Context,
        callback: SimplePermissionCallback,
        contentView: View
    ){
        workerScope.launch {
            val feedbackViewPermissionListener = object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {

                        if (report.grantedPermissionResponses.size>0) {
                            Log.d("permission_debug", "onPermissionGranted: ")
                            callback.onPermissionGranted()
                        }else{
                            Log.d("permission_debug", " areAllPermissionsGranted false: ")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Log.d("permission_debug", "onPermissionRationaleShouldBeShown: ")
                    AlertDialog.Builder(context).setTitle("We need this permission!")
                        .setMessage("The permission is needed to know your location")
                        .setNegativeButton(R.string.cancel
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

            }
            val locationPermissionListener = CompositeMultiplePermissionsListener(
                feedbackViewPermissionListener,
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(
                    contentView,
                    "Location permission is needed to get your current location"
                )
                    .withOpenSettingsButton("Settings")
                    .withCallback(object : Snackbar.Callback() {
                        override fun onShown(snackbar: Snackbar) {
                            super.onShown(snackbar)
                        }

                        override fun onDismissed(snackbar: Snackbar, event: Int) {
                            super.onDismissed(snackbar, event)
                        }
                    })
                    .build(),
            )
            Dexter.withContext(AdmeApplication.APP_CONTEXT)
                .withPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(locationPermissionListener)
                .withErrorListener {
                    Log.d("permission_debug", "withErrorListener: " + it?.name)
                }
                .onSameThread()
                .check()
        }
    }


    interface SimplePermissionCallback{
        fun onPermissionGranted()
        fun onPermissionDenied()
    }
}