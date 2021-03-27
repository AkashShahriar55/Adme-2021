package com.cookietech.namibia.adme.managers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.cookietech.namibia.adme.utils.PermissionHelper.REQUEST_CHECK_SETTINGS
import com.cookietech.namibia.adme.utils.SingleLiveEvent
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationManager {


    fun fetchCurrentLocation(context:Activity,location:SingleLiveEvent<Location?>){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("location_debug", "permission not given: ")
            return
        }
        createLocationRequest(context).addOnSuccessListener {
            Log.d("location_debug", "location request successfull ")
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (loc in locationResult.locations){
                        // Update UI with location data
                        // ...
                            location.value = loc
                        locationProviderClient.removeLocationUpdates(this)
                        Log.d("location_debug", "location fetched: ")
                    }
                }
            }
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())

            locationProviderClient.lastLocation.addOnSuccessListener {
                location.value = it
            }
        }.addOnFailureListener {exception->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(context,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    fun createLocationRequest(activity:Activity): Task<LocationSettingsResponse> {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        return client.checkLocationSettings(builder.build())
    }
}