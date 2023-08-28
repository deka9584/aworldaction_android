package com.example.aworldaction.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class ContributorAdapter(private var dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<ContributorAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView
        val name: TextView

        init {
            image = view.findViewById(R.id.userPicture)
            name = view.findViewById(R.id.userName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contributor_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val contributorJSON = dataSet[position]

        if (contributorJSON.has("name")) {
            val name = contributorJSON.getString("name")
            viewHolder.name.text = name
        }

        viewHolder.name.text = contributorJSON.optString("name")

        if (contributorJSON.has("picture_path")) {
            val imageUrl = AppSettings.getStorageUrl(contributorJSON.getString("picture_path"))

            if (imageUrl != null) {
                Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(viewHolder.image)
            }
        } else {
            viewHolder.image.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(newList: List<JSONObject>) {
        dataSet = newList
        notifyDataSetChanged()
    }
}