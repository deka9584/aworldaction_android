package com.example.aworldaction.managers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.core.app.ActivityCompat
import com.example.aworldaction.R
import java.io.IOException
import java.util.*

class AppLocationManager (val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: LocationListener? = null

    fun startListener(locationListener: LocationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            if (listener == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                listener = locationListener
            }
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }
    }

    fun stopListener() {
        listener?.let {
            locationManager.removeUpdates(it)
            listener = null
        }
    }

    fun getLocality(location: Location): String? {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                return addresses[0].locality
                    ?: context.resources.getString(R.string.unknown_locality)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return context.resources.getString(R.string.unknown_locality)
    }
}