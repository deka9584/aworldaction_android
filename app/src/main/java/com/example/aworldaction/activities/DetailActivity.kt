package com.example.aworldaction.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.fragments.EditCommentFragment
import com.example.aworldaction.fragments.MapsFragment
import com.example.aworldaction.adapters.CommentAdapter
import com.example.aworldaction.adapters.ContributorAdapter
import com.example.aworldaction.adapters.SlideshowAdapter
import com.example.aworldaction.models.DetailActivityModel
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {
    private lateinit var model: DetailActivityModel
    private var campaignId = 0
    private var editCommentFragment: EditCommentFragment? = null
    private var picturesDisplay: ViewPager? = null
    private var contributorsDisplay: RecyclerView? = null
    private var commentsDisplay: RecyclerView? = null
    private var titleDisplay: TextView? = null
    private var localityDisplay: TextView? = null
    private var descriptionDisplay: TextView? = null
    private var statusImgDisplay: ImageView? = null
    private var statusTxtDisplay: TextView? = null
    private var userPicture: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (!::model.isInitialized) {
            model = ViewModelProvider(this)[DetailActivityModel::class.java]
        }

        campaignId = intent.getIntExtra("campaignId", 0)
        editCommentFragment = EditCommentFragment()

        picturesDisplay = findViewById(R.id.picturesDisplay)
        titleDisplay = findViewById(R.id.campaignTitle)
        localityDisplay = findViewById(R.id.localityDisplay)
        descriptionDisplay = findViewById(R.id.descriptionDisplay)
        statusImgDisplay = findViewById(R.id.statusImgDisplay)
        statusTxtDisplay = findViewById(R.id.statusTxtDisplay)
        userPicture = findViewById(R.id.userPicture)

        contributorsDisplay = findViewById(R.id.contributorList)
        contributorsDisplay?.layoutManager = LinearLayoutManager(this)
        contributorsDisplay?.isNestedScrollingEnabled = false
        contributorsDisplay?.adapter = ContributorAdapter(emptyList(), this)

        commentsDisplay = findViewById(R.id.commentList)
        commentsDisplay?.layoutManager = LinearLayoutManager(this)
        commentsDisplay?.isNestedScrollingEnabled = false
        commentsDisplay?.adapter = CommentAdapter(emptyList(), this)

        model.campaign.observe(this, Observer { campaign ->
            showCampaign(campaign)
        })

        model.pictures.observe(this, Observer { pictures ->
            picturesDisplay?.adapter = SlideshowAdapter(pictures, this)
            picturesDisplay?.visibility = if (pictures.isEmpty()) View.GONE else View.VISIBLE
        })

        model.contributors.observe(this, Observer { contributors ->
            (contributorsDisplay?.adapter as? ContributorAdapter)?.setData(contributors)
        })

        model.comments.observe(this, Observer { comments ->
            showComments(comments)
        })

        if (campaignId != 0) {
            model.loadCampaign(this, campaignId)
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val pictureBtn = findViewById<ImageButton>(R.id.pictureBtn)
        pictureBtn.setOnClickListener {
            val intent = Intent(this, UploadCampaignPictureActivity::class.java)
            intent.putExtra("campaignId", campaignId)
            startActivity(intent)
        }

        val sendCommentBtn = findViewById<ImageButton>(R.id.sendCommentBtn)
        val commentText = findViewById<EditText>(R.id.commentText)
        sendCommentBtn.setOnClickListener {
            if (commentText.text.isNotBlank()) {
                model.sendComment(this, campaignId, commentText.text.toString())
                commentText.text.clear()
                commentText.clearFocus()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        model.loadCampaign(this, campaignId)
    }

    private fun showCampaign(campaign: JSONObject) {
        titleDisplay?.text = campaign.optString("name")
        localityDisplay?.text = campaign.optString("location_name")
        descriptionDisplay?.text = campaign.optString("description")

        if (campaign.has("location_lat") || campaign.has("location_lng")) {
            val lat = campaign.getDouble("location_lat")
            val lng = campaign.getDouble("location_lng")

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapsFragment, MapsFragment.newInstance(lat, lng))
                .commit()
        }

        if (campaign.optInt("completed") == 1) {
            statusImgDisplay?.setImageResource(R.drawable.ic_baseline_done_all_24)
            statusImgDisplay?.setColorFilter(getColor(R.color.green))
            statusTxtDisplay?.text = resources.getString(R.string.campaign_completed)
            statusTxtDisplay?.setTextColor(getColor(R.color.green))
        } else {
            statusImgDisplay?.setImageResource(R.drawable.ic_baseline_access_time_24)
            statusImgDisplay?.setColorFilter(getColor(R.color.orange))
            statusTxtDisplay?.text = resources.getString(R.string.campaign_inprogress)
            statusTxtDisplay?.setTextColor(getColor(R.color.orange))
        }

        model.loadComments(this, campaignId)
    }

    private fun showComments(comments: List<JSONObject>) {
        userPicture?.let {
            val path = AppSettings.getUser()?.optString("picture_path")
            val url = if (path?.isNotBlank() == true) AppSettings.getStorageUrl(path) else null

            if (url != null) {
                Glide.with(this)
                    .load(url)
                    .into(it)
            } else {
                it.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }

        (commentsDisplay?.adapter as? CommentAdapter)?.setData(comments)
    }

    fun showEditCommentFragment(commentId: Int, commentBody: String) {
        editCommentFragment?.setComment(commentId, commentBody)
        editCommentFragment?.show(supportFragmentManager, "EditCommentFragmentTag")
    }

    fun confirmEditComment(commentId: Int, commentBody: String) {
        model.updateComment(this, commentId, commentBody)
        editCommentFragment?.dismiss()
        editCommentFragment?.setComment(0, "")
    }

    fun showDeleteCommentDialog(commentId: Int) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.delete_comment))
        builder.setMessage(resources.getString(R.string.delete_comment_confirm))

        builder.setPositiveButton(resources.getString(R.string.confirm_btn)) { dialog, which ->
            model.deleteComment(this, commentId)
            dialog.cancel()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel_btn)) { dialog, which ->
            dialog.cancel()
        }

        builder.create().show()
    }

    fun showDeletePictureDialog(pictureId: Int) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.delete_picture))
        builder.setMessage(resources.getString(R.string.delete_picture_confirm))

        builder.setPositiveButton(resources.getString(R.string.confirm_btn)) { dialog, which ->
            model?.deletePicture(this, pictureId)
            dialog.cancel()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel_btn)) { dialog, which ->
            dialog.cancel()
        }

        builder.create().show()
    }
}