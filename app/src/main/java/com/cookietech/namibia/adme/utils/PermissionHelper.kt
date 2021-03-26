package com.cookietech.namibia.adme.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

object PermissionHelper {
    private const val TAG = "PermissionHelper"
    const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    const val REQUEST_CHECK_SETTINGS = 2
    fun requestLocationPermission(context: Context): Boolean {
        return checkPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
            "Location Permission",
            "Location permission is must for the map features"
        )
    }

    private fun checkPermission(
        context: Context,
        permission_type: String,
        permission_id: Int,
        permission_rational_title: String,
        permission_rational_message: String
    ): Boolean {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                context,
                permission_type
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity),
                    permission_type
                )
            ) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(permission_rational_title).setMessage(permission_rational_message)
                    .setPositiveButton("Proceed") { dialog, which -> // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(
                            context, arrayOf(permission_type),
                            permission_id
                        )
                    }
                    .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }.show()
            } else {
                Log.i(TAG, "checkPermission: permission not granted")
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    context, arrayOf(permission_type),
                    permission_id
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i(TAG, "checkPermission: permission granted")
            return true
        }
        return false
    }

    fun checkSettingsForLocation(context: Context?): Task<LocationSettingsResponse> {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        return client.checkLocationSettings(builder.build())
    }
}