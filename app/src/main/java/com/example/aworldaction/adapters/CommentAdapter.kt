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
import org.w3c.dom.Text

class CommentAdapter(private val dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView
        val userName: TextView
        val commentText: TextView
        val commentEdited: TextView
        val editBtn: ImageButton
        val deleteBtn: ImageButton

        init {
            userImage = view.findViewById(R.id.userPicture)
            userName = view.findViewById(R.id.userName)
            commentText = view.findViewById(R.id.commentText)
            commentEdited = view.findViewById(R.id.commentEdited)
            editBtn = view.findViewById(R.id.editBtn)
            deleteBtn = view.findViewById(R.id.deleteBtn)
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
            val buttonVisibility =
                if (comment.getInt("user_id") == AppSettings.getUser()?.getInt("id")) View.VISIBLE
                else View.GONE

            viewHolder.editBtn.visibility = buttonVisibility
            viewHolder.deleteBtn.visibility = buttonVisibility

            viewHolder.deleteBtn.setOnClickListener {
                val activity = context as? DetailActivity
                activity?.showDeleteCommentDialog(comment.getInt("id"))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}