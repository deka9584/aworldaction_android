package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.MapsFragment
import com.example.aworldaction.adapters.ContributorAdapter
import com.example.aworldaction.adapters.SlideshowAdapter
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {
    private var campaign: JSONObject? = null
    private var pictures = ArrayList<JSONObject>()
    private var contributors = ArrayList<JSONObject>()
    private var picutresDisplay: ViewPager? = null
    private var contributorsDisplay: RecyclerView? = null
    private var titleDisplay: TextView? = null
    private var localityDisplay: TextView? = null
    private var descriptionDisplay: TextView? = null
    private var statusImgDisplay: ImageView? = null
    private var statusTxtDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        picutresDisplay = findViewById(R.id.picturesDisplay)

        contributorsDisplay = findViewById(R.id.contributorList)
        contributorsDisplay?.layoutManager = LinearLayoutManager(this)
        contributorsDisplay?.adapter = ContributorAdapter(contributors, this)

        titleDisplay = findViewById(R.id.campaignTitle)
        localityDisplay = findViewById(R.id.localityDisplay)
        descriptionDisplay = findViewById(R.id.descriptionDisplay)

        statusImgDisplay = findViewById(R.id.statusImgDisplay)
        statusTxtDisplay = findViewById(R.id.statusTxtDisplay)

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
                        this.pictures.add(pictures.getJSONObject(i))
                    }
                }

                val contributors = campaign?.getJSONArray("contributors")
                contributors.let {
                    for (i in 0 until contributors!!.length()) {
                        this.contributors.add(contributors.getJSONObject(i))
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
        val slideshowAdapter = SlideshowAdapter(pictures, this)
        picutresDisplay?.adapter = slideshowAdapter

        val contributorAdapter = ContributorAdapter(contributors, this)
        contributorsDisplay?.adapter = contributorAdapter

        campaign?.let { campaign ->
            if (campaign.has("name")) {
                titleDisplay?.text = campaign.getString("name")
            }

            if (campaign.has("location_name")) {
                localityDisplay?.text = campaign.getString("location_name")
            }

            if (campaign.has("description")) {
                descriptionDisplay?.text = campaign.getString("description")
            }

            if (campaign.has("location_lat") || campaign.has("location_lng")) {
                val lat = campaign.getDouble("location_lat")
                val lng = campaign.getDouble("location_lng")

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mapsFragment, MapsFragment.newInstance(lat, lng))
                    .commit()
            }

            if (campaign.has("completed")) {
                if (campaign.getInt("completed") == 1) {
                    statusImgDisplay?.setImageResource(R.drawable.ic_baseline_done_all_24)
                    statusImgDisplay?.setColorFilter(resources.getColor(R.color.green, theme))
                    statusTxtDisplay?.text = resources.getString(R.string.campaign_completed)
                    statusTxtDisplay?.setTextColor(resources.getColor(R.color.green, theme))
                } else {
                    statusImgDisplay?.setImageResource(R.drawable.ic_baseline_access_time_24)
                    statusImgDisplay?.setColorFilter(resources.getColor(R.color.orange, theme))
                    statusTxtDisplay?.text = resources.getString(R.string.campaign_inprogress)
                    statusTxtDisplay?.setTextColor(resources.getColor(R.color.orange, theme))
                }
            }
        }
    }
}