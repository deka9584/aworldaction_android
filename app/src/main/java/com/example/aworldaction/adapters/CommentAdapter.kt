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
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject
import org.w3c.dom.Text

class CommentAdapter(private var dataSet: List<JSONObject>, private val context: Context) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
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
        val loggedUserIsCreator = comment.optInt("user_id") == AppSettings.getUserID()

        viewHolder.userName.text = comment.optString("user_name")
        viewHolder.commentText.text = comment.optString("body")

        if (comment.has("picture_path")) {
            val url = AppSettings.getStorageUrl(comment.getString("picture_path"))

            if (url != null) {
                Glide.with(context)
                    .load(url)
                    .into(viewHolder.userImage)
            }
        } else {
            viewHolder.userImage.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }

        if (comment.has("created_at") && comment.has("updated_at")) {
            val createdAt = comment.getString("created_at")
            val updatedAt = comment.getString("updated_at")

            viewHolder.commentEdited.visibility =
                if (!createdAt.equals(updatedAt)) View.VISIBLE else View.GONE
        }

        if (loggedUserIsCreator) {
            val activity = context as? DetailActivity

            viewHolder.editBtn.visibility = View.VISIBLE
            viewHolder.editBtn.setOnClickListener {
                if (comment.has("id") && comment.has("body")) {
                    activity?.showEditCommentFragment(
                        comment.getInt("id"),
                        comment.getString("body")
                    )
                }
            }

            viewHolder.deleteBtn.visibility = View.VISIBLE
            viewHolder.deleteBtn.setOnClickListener {
                if (comment.has("id")) {
                    activity?.showDeleteCommentDialog(comment.getInt("id"))
                }
            }
        } else {
            viewHolder.editBtn.visibility = View.GONE
            viewHolder.deleteBtn.visibility = View.GONE
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