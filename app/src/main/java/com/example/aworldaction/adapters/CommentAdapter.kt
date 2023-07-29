package com.example.aworldaction.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class CommentAdapter(private val dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView
        val userName: TextView
        val commentText: TextView
        val editBtn: ImageButton

        init {
            userImage = view.findViewById(R.id.userPicture)
            userName = view.findViewById(R.id.userName)
            commentText = view.findViewById(R.id.commentText)
            editBtn = view.findViewById(R.id.editBtn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val comment = dataSet[position]

        if (comment.has("picture_path")) {
            val url = AppSettings.getStorageUrl(comment.getString("picture_path"))

            Glide.with(context)
                .load(url)
                .into(viewHolder.userImage)
        }

        if (comment.has("user_name")) {
            viewHolder.userName.text = comment.getString("user_name")
        }

        if (comment.has("body")) {
            viewHolder.commentText.text = comment.getString("body")
        }

        if (comment.has("user_id")) {
            viewHolder.editBtn.visibility =
                if (comment.getInt("user_id") == AppSettings.getUser()?.getInt("id")) View.VISIBLE
                else View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}