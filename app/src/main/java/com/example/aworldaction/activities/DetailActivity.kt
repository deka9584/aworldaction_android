package com.example.aworldaction.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.MapsFragment
import com.example.aworldaction.adapters.CommentAdapter
import com.example.aworldaction.adapters.ContributorAdapter
import com.example.aworldaction.adapters.SlideshowAdapter
import com.example.aworldaction.managers.CampaignDetailManager
import com.example.aworldaction.settings.AppSettings

class DetailActivity : AppCompatActivity() {
    private var cdm: CampaignDetailManager? = null
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

        cdm = CampaignDetailManager(this)

        picturesDisplay = findViewById(R.id.picturesDisplay)
        contributorsDisplay = findViewById(R.id.contributorList)
        contributorsDisplay?.layoutManager = LinearLayoutManager(this)
        commentsDisplay = findViewById(R.id.commentList)
        commentsDisplay?.layoutManager = LinearLayoutManager(this)

        cdm?.let {
            contributorsDisplay?.adapter = ContributorAdapter(it.getContributors(), this)
            commentsDisplay?.adapter = ContributorAdapter(it.getComments(), this)
        }

        titleDisplay = findViewById(R.id.campaignTitle)
        localityDisplay = findViewById(R.id.localityDisplay)
        descriptionDisplay = findViewById(R.id.descriptionDisplay)
        statusImgDisplay = findViewById(R.id.statusImgDisplay)
        statusTxtDisplay = findViewById(R.id.statusTxtDisplay)
        userPicture = findViewById<ImageView>(R.id.userPicture)

        val campaignId = intent.getIntExtra("campaignId", 0)
        if (campaignId != 0) {
            cdm?.loadCampaign(campaignId)
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
                cdm?.sendComment(commentText.text.toString())
                commentText.text.clear()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        cdm?.getCampaign()?.let {
            if (it.has("id")) {
                cdm?.loadCampaign(it.getInt("id"))
            }
        }
    }

    fun showCampaignData() {
        cdm?.let {
            val pictures = it.getPictures()

            if (pictures.size > 0) {
                val slideshowAdapter = SlideshowAdapter(pictures, this)
                picturesDisplay?.adapter = slideshowAdapter
                picturesDisplay?.visibility = View.VISIBLE
            } else {
                picturesDisplay?.visibility = View.GONE
            }

            val contributorAdapter = ContributorAdapter(it.getContributors(), this)
            contributorsDisplay?.adapter = contributorAdapter
        }

        cdm?.getCampaign()?.let {
            if (it.has("name")) {
                titleDisplay?.text = it.getString("name")
            }

            if (it.has("location_name")) {
                localityDisplay?.text = it.getString("location_name")
            }

            if (it.has("description")) {
                descriptionDisplay?.text = it.getString("description")
            }

            if (it.has("location_lat") || it.has("location_lng")) {
                val lat = it.getDouble("location_lat")
                val lng = it.getDouble("location_lng")

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mapsFragment, MapsFragment.newInstance(lat, lng))
                    .commit()
            }

            if (it.has("completed")) {
                if (it.getInt("completed") == 1) {
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

    fun showComments() {
        userPicture?.let {
            val user = AppSettings.getUser()

            if (user != null && user.has("picture_path")) {
                val url = AppSettings.getStorageUrl(user.getString("picture_path"))
                Glide.with(this)
                    .load(url)
                    .into(it)
            }
        }

        cdm?.let {
            commentsDisplay?.adapter = CommentAdapter(it.getComments(), this)
        }
    }
}