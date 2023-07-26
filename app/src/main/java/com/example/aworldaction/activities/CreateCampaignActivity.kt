package com.example.aworldaction.activities

import android.content.Intent
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.MapsFragment
import com.example.aworldaction.managers.AppLocationManager
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.HashMap

class CreateCampaignActivity : AppCompatActivity() {
    private var appLocationManager: AppLocationManager? = null
    private var locationListener: LocationListener? = null
    private var userLocation: Location? = null
    private var progressBar: ProgressBar? = null
    private var titleField: EditText? = null
    private var captionField: EditText? = null
    private var localityDisplay: TextView? = null
    private var statusDisplay: TextView? = null
    private var mapsFragment: MapsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)

        appLocationManager = AppLocationManager(this)

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                showMap(location)
                userLocation = location
                localityDisplay?.text = appLocationManager?.getLocality(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        progressBar = findViewById(R.id.progressBar)
        titleField = findViewById(R.id.titleField)
        captionField = findViewById(R.id.captionField)
        localityDisplay = findViewById(R.id.localityDisplay)
        statusDisplay = findViewById(R.id.authStatus)

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

        locationListener?.let {
            appLocationManager?.startListener(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty()) {
            locationListener?.let {
                appLocationManager?.startListener(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appLocationManager?.stopListener()
    }

    private fun showMap(loc: Location) {
        val frame = findViewById<FrameLayout>(R.id.mapsFragment)

        if (mapsFragment == null) {
            mapsFragment = MapsFragment.newInstance(loc.latitude, loc.longitude)

            supportFragmentManager
                .beginTransaction()
                .replace(frame.id, mapsFragment!!)
                .commit()

            frame.visibility = View.VISIBLE
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
                statusDisplay?.text = message
                statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())

            try {
                val jsonResponse = JSONObject(responseString)
                statusDisplay?.text = "${error.networkResponse?.statusCode}"

                if (jsonResponse.has("message")) {
                    statusDisplay?.text = jsonResponse.getString("message")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.e("serverAPI", error.toString())
            statusDisplay?.setTextColor(resources.getColor(R.color.red, theme))
            progressBar?.visibility = View.INVISIBLE
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = "${titleField?.text}"
                params["description"] = "${captionField?.text}"
                params["location_name"] = "${localityDisplay?.text}"
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