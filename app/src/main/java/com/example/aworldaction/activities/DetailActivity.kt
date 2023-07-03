package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.ListFragment
import com.example.aworldaction.activities.fragments.MapsFragment
import com.example.aworldaction.adapters.CampaignAdapter
import com.example.aworldaction.adapters.SlideshowAdapter
import com.example.aworldaction.settings.AppSettings
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {
    private var campaign: JSONObject? = null
    private var picturesPaths = ArrayList<String>()
    private var picutresDisplay: ViewPager? = null
    private var titleDisplay: TextView? = null
    private var localityDisplay: TextView? = null
    private var descriptionDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        picutresDisplay = findViewById(R.id.picturesDisplay)
        titleDisplay = findViewById(R.id.campaignTitle)
        localityDisplay = findViewById(R.id.localityDisplay)
        descriptionDisplay = findViewById(R.id.descriptionDisplay)

        val campaingId = intent.getIntExtra("campaignId", 0)
        if (campaingId != 0) {
            loadCampaign(campaingId)
        }

        val backBtn: ImageButton? = findViewById(R.id.backBtn)
        backBtn?.setOnClickListener {
            finish()
        }
    }

    private fun loadCampaign(campaingId: Int) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/campaigns/$campaingId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data")) {
                campaign = responseJSON.getJSONObject("data")

                val pictures = campaign?.getJSONArray("pictures")
                pictures.let {
                    for (i in 0 until pictures!!.length()) {
                        picturesPaths.add(pictures.getJSONObject(i).getString("path"))
                    }
                }

                showCampaignData()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + AppSettings.getToken()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }
        requestQueue.add(request)
    }

    private fun showCampaignData() {
        val adapter = SlideshowAdapter(picturesPaths, this)
        picutresDisplay?.adapter = adapter

        titleDisplay?.text = campaign?.getString("name")
        localityDisplay?.text = campaign?.getString("location_name")
        descriptionDisplay?.text = campaign?.getString("description")

        val lat = campaign?.getDouble("location_lat")
        val lng = campaign?.getDouble("location_lng")

        if (lat != null && lng != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapsFragment, MapsFragment.newInstance(lat, lng))
                .commit()
        }
    }
}