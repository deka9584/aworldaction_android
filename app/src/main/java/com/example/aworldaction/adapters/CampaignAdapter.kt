package com.example.aworldaction.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class CampaignAdapter(private val dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<CampaignAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView
        val title: TextView
        val locality: TextView
        val description: TextView
        val detailBtn: Button
        val favouritesBtn: ImageButton
        val mapsBtn: ImageButton

        init {
            image = view.findViewById(R.id.campaignPicture)
            title = view.findViewById(R.id.campaignTitle)
            locality = view.findViewById(R.id.localityDisplay)
            description = view.findViewById(R.id.description)
            detailBtn = view.findViewById(R.id.detailBtn)
            favouritesBtn = view.findViewById(R.id.favouritesBtn)
            mapsBtn = view.findViewById(R.id.mapsBtn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.campaign_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val campaign = dataSet[position]

        if (campaign.has("name")) {
            viewHolder.title.text = campaign.getString("name")
        }

        if (campaign.has("location_name")) {
            viewHolder.locality.text = campaign.getString("location_name")
        }

        if (campaign.has("description")) {
            viewHolder.description.text = campaign.getString("description")
        }

        if (campaign.has("pictures")) {
            val pictures = campaign.getJSONArray("pictures")

            if (pictures.length() > 0) {
                val firstPicture = pictures.getJSONObject(0)

                if (firstPicture.has("path")) {
                    val url = AppSettings.getStorageUrl(firstPicture.getString("path"))

                    Glide.with(context)
                        .load(url)
                        .into(viewHolder.image)

                }
            } else {
                Glide.with(context)
                    .load(context.resources.getDrawable(R.mipmap.picture_placeholder_foreground, context.theme))
                    .into(viewHolder.image)
            }
        }

        if (campaign.has("contributors")) {
            val contributors = campaign.getJSONArray("contributors")

            for (i in 0 until contributors.length()) {
                if (contributors.getJSONObject(i).getInt("id") == AppSettings.getUser()?.getInt("id")) {
                    viewHolder.favouritesBtn.setImageResource(R.drawable.ic_baseline_star_24)
                }
            }
        }

        viewHolder.detailBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("campaignId", campaign.getInt("id"))
            context.startActivity(intent)
        }

        viewHolder.favouritesBtn.setOnClickListener {
            toggleFavourites(campaign.getInt("id"), viewHolder.favouritesBtn)
        }

        viewHolder.mapsBtn.setOnClickListener {
            if (campaign.has("location_lat") && campaign.has("location_lng")) {
                val lat = campaign.getDouble("location_lat")
                val lng = campaign.getDouble("location_lng")
                val url = "https://maps.google.com/maps?q=+${lat}+,+${lng}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private fun toggleFavourites(campaignId: Int, favouritesBtn: ImageButton) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = AppSettings.getAPIUrl().toString() + "/relate-campaign/logged"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                if (message == "Campaign detached") {
                    favouritesBtn.setImageResource(R.drawable.ic_baseline_star_border_24)
                } else if (message == "Campaign attached") {
                    favouritesBtn.setImageResource(R.drawable.ic_baseline_star_24)
                }

                Log.d("serverApi", responseJSON.getString("message"))
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["campaign_id"] = "$campaignId"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + AppSettings.getToken()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }
}