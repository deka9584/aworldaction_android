package com.example.aworldaction.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class CampaignAdapter(private val dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<CampaignAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView
        val title: TextView
        val description: TextView
        val detailBtn: Button

        init {
            title = view.findViewById(R.id.campaignTitle)
            description = view.findViewById(R.id.description)
            image = view.findViewById(R.id.campaignPicture)
            detailBtn = view.findViewById(R.id.detailBtn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_campaign_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val campaign = dataSet[position]
        viewHolder.title.text = campaign.getString("name")
        viewHolder.description.text = campaign.getString("description")

        val pictures = campaign.getJSONArray("pictures")

        if (pictures.length() > 0) {
            val firstPicture = pictures.getJSONObject(0)

            if (firstPicture.has("path")) {
                val url = AppSettings.getStorageUrl(firstPicture.getString("path"))

                Glide.with(context)
                    .load(url)
                    .into(viewHolder.image)
            }
        }

        viewHolder.detailBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("campaignId", campaign.getInt("id")) // Aggiungi il dato all'Intent con una chiave e un valore
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}