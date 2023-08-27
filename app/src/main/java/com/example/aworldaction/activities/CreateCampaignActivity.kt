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
import com.example.aworldaction.clients.ContentApiClient
import com.example.aworldaction.fragments.MapsFragment
import com.example.aworldaction.managers.AppLocationManager
import com.example.aworldaction.requests.RequestsHelper
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
    private var mapsFrame: FrameLayout? = null
    private var captionField: EditText? = null
    private var localityDisplay: TextView? = null
    private var statusDisplay: TextView? = null

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
        mapsFrame = findViewById(R.id.mapsFragment)
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
        mapsFrame?.let {
            val mapsFragment = MapsFragment.newInstance(loc.latitude, loc.longitude)

            supportFragmentManager
                .beginTransaction()
                .replace(it.id, mapsFragment)
                .commit()

            it.visibility = View.VISIBLE
        }

        appLocationManager?.stopListener()
    }

    private fun startDetailActivity(campaignId: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("campaignId", intent)
        startActivity(intent)
    }

    private fun storeCampaign() {
        val params = HashMap<String, String>()
        params["name"] = "${titleField?.text}"
        params["description"] = "${captionField?.text}"
        params["location_name"] = "${localityDisplay?.text}"
        params["location_lat"] = "${userLocation?.latitude}"
        params["location_lng"] = "${userLocation?.longitude}"

        progressBar?.visibility = View.VISIBLE

        ContentApiClient.postCampaign(this, params,
            onSuccess = { response ->
                response.optJSONObject("campaign")?.let {
                    startDetailActivity(it.optInt("id"))
                    finish()
                }

                progressBar?.visibility = View.INVISIBLE
            },
            onError = { message ->
                statusDisplay?.text = message
                statusDisplay?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            }
        )
    }
}