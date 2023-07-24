package com.example.aworldaction.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.MapsFragment
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class CreateCampaignActivity : AppCompatActivity() {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var progressBar: ProgressBar? = null
    private var titleField: EditText? = null
    private var captionField: EditText? = null
    private var localityDisplay: TextView? = null
    private var serverMessage: TextView? = null
    private var userLocation: Location? = null
    private var userLocality: String? = null
    private var mapsFragment: MapsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        progressBar = findViewById(R.id.progressBar)
        titleField = findViewById(R.id.titleField)
        captionField = findViewById(R.id.captionField)
        localityDisplay = findViewById(R.id.localityDisplay)
        serverMessage = findViewById(R.id.authStatus)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn?.setOnClickListener {
            if (
                titleField?.text?.isBlank() == false &&
                captionField?.text?.isBlank() == false &&
                userLocation != null
            ) {
                storeCampaign()
            }
        }

        localityDisplay?.text = resources.getString(R.string.waiting_locatoin)
        readUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty()) {
            readUserLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (locationListener != null) {
            locationManager?.removeUpdates(locationListener!!)
        }
    }

    private fun readUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            if (locationListener == null) {
                locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        userLocation = location
                        showMap()
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener!!)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }
    }

    private fun showMap() {
        if (userLocation != null) {
            val frame = findViewById<FrameLayout>(R.id.mapsFragment)
            val loc = userLocation!!

            if (mapsFragment == null) {
                mapsFragment = MapsFragment.newInstance(loc.latitude, loc.longitude)

                supportFragmentManager
                    .beginTransaction()
                    .replace(frame.id, mapsFragment!!)
                    .commit()

                frame.visibility = View.VISIBLE
            }

            val geocoder = Geocoder(this, Locale.getDefault())

            try {
                val addresses: List<Address>? = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)

                if (addresses != null && addresses.isNotEmpty()) {
                    val locality = addresses[0].locality ?: resources.getString(R.string.unknown_locality)
                    localityDisplay?.text = locality
                    userLocality = locality
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun storeCampaign() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/campaigns"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("campaign")) {
                val campaign = responseJSON.getJSONObject("campaign")

                if (campaign.has("id")) {
                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra("campaignId", campaign.getInt("id"))
                    startActivity(intent)
                }

                finish()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                serverMessage?.text = message
                serverMessage?.setTextColor(resources.getColor(R.color.green, theme))
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())
            Log.e("serverAPI", error.toString())

            try {
                val jsonResponse = JSONObject(responseString)
                val errorMessage = jsonResponse.getString("message")
                serverMessage?.text = errorMessage
            } catch (e: JSONException) {
                e.printStackTrace()
                serverMessage?.text = "${error.networkResponse.statusCode}"
            }

            serverMessage?.setTextColor(resources.getColor(R.color.red, theme))
            progressBar?.visibility = View.INVISIBLE
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = titleField?.text.toString()
                params["description"] = captionField?.text.toString()
                params["location_name"] = "$userLocality"
                params["location_lat"] = "${userLocation?.latitude}"
                params["location_lng"] = "${userLocation?.longitude}"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        progressBar?.visibility = View.VISIBLE
        requestQueue.add(request)
    }
}