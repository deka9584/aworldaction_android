package com.example.aworldaction.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.clients.ContentApiClient
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class CampaignAdapter(private var dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<CampaignAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView
        val progress: TextView
        val title: TextView
        val locality: TextView
        val description: TextView
        val detailBtn: Button
        val favouritesBtn: ImageButton
        val mapsBtn: ImageButton

        init {
            image = view.findViewById(R.id.campaignPicture)
            progress = view.findViewById(R.id.progressDisplay)
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

        viewHolder.title.text = campaign.optString("name")
        viewHolder.locality.text = campaign.optString("location_name")
        viewHolder.description.text = campaign.optString("description")

        campaign.optJSONArray("pictures")?.let {
            if (it.length() > 0) {
                val path = it.getJSONObject(0).optString("path")
                val url = AppSettings.getStorageUrl(path)

                Glide.with(context)
                    .load(url)
                    .into(viewHolder.image)
            } else {
                Glide.with(context)
                    .load(context.resources.getDrawable(R.mipmap.picture_placeholder_foreground, context.theme))
                    .into(viewHolder.image)
            }
        }

        viewHolder.progress.visibility =
            if (campaign.optInt("completed") == 1) View.VISIBLE else View.GONE

        campaign.optJSONArray("contributors")?.let {
            var isUserContributor = false

            for (i in 0 until it.length()) {
                if (it.getJSONObject(i).getInt("id") == AppSettings.getUserID()) {
                    isUserContributor = true
                    break
                }
            }

            viewHolder.favouritesBtn.setImageResource(
                if (isUserContributor) R.drawable.ic_baseline_star_24
                else R.drawable.ic_baseline_star_border_24
            )
        }

        campaign.optJSONArray("creator_id")?.let {
            for (i in 0 until it.length()) {
                if (it.getInt(i) == AppSettings.getUserID()) {
                    viewHolder.favouritesBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
                    viewHolder.favouritesBtn.isEnabled = false
                    break
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
        ContentApiClient.toggleCampaignFavourite(context, campaignId,
            onSuccess = { response ->
                val message = response.optString("message")

                when (message) {
                    "Campaign detached" -> {
                        favouritesBtn.setImageResource(R.drawable.ic_baseline_star_border_24)
                    }
                    "Campaign attached" -> {
                        favouritesBtn.setImageResource(R.drawable.ic_baseline_star_24)
                    }
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun setData(newList: List<JSONObject>) {
        dataSet = newList
        notifyDataSetChanged()
    }
}