package com.example.aworldaction.activities

import com.example.aworldaction.requests.VolleyMultipartRequest
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.clients.ContentApiClient
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class UploadCampaignPictureActivity : AppCompatActivity() {
    private var campaignId = 0
    private var progressBar: ProgressBar? = null
    private var pictureDisplay: ImageView? = null
    private var pictureCaption: EditText? = null
    private var picturePlaceholder: LinearLayout? = null
    private var statusDisplay: TextView? = null
    private var imageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_campaign_picture)

        campaignId = intent.getIntExtra("campaignId", 0)
        progressBar = findViewById(R.id.progressBar)
        pictureDisplay = findViewById(R.id.pictureDisplay)
        pictureCaption = findViewById(R.id.pictureCaption)
        picturePlaceholder = findViewById(R.id.imagePlaceholder)
        statusDisplay = findViewById(R.id.statusDisplay)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                previewImage(uri)
                imageSelected = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val pickerBtn = findViewById<Button>(R.id.pickerBtn)
        pickerBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val submitBtn= findViewById<Button>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            if (campaignId != 0 && pictureCaption?.text?.isBlank() == false && imageSelected) {
                pictureDisplay?.let {
                    uploadImage(it.drawable)
                }
            }
        }
    }

    private fun previewImage(uri: Uri) {
        pictureDisplay?.let {
            Glide.with(this)
                .load(uri)
                .into(it)
            it.visibility = View.VISIBLE
        }

        picturePlaceholder?.visibility = View.GONE
    }

    private fun uploadImage(image: Drawable) {
        val params = HashMap<String, String>()
        params["campaign_id"] = "$campaignId"
        params["caption"] = "${pictureCaption?.text}"

        progressBar?.visibility = View.VISIBLE

        ContentApiClient.postPicture(this, params, image,
            onSuccess = { response ->
                val message = response.optString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                Log.d("serverApi", message)
                progressBar?.visibility = View.INVISIBLE
                finish()
            },
            onError = { message ->
                statusDisplay?.text = message
                statusDisplay?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            }
        )
    }
}