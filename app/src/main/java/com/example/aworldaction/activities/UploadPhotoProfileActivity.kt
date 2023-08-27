package com.example.aworldaction.activities

import com.example.aworldaction.requests.VolleyMultipartRequest
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.clients.AuthApiClient
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject


class UploadPhotoProfileActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var pictureDisplay: ImageView? = null
    private var statusDisplay: TextView? = null
    private var pictureSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo_profile)

        progressBar = findViewById(R.id.progressBar)
        pictureDisplay = findViewById(R.id.pictureDisplay)
        statusDisplay = findViewById(R.id.statusDisplay)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                previewImage(uri)
                pictureSelected = true
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
            if (progressBar?.isVisible == false) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        val uploadBtn = findViewById<Button>(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            if (pictureSelected && progressBar?.isVisible == false) {
                pictureDisplay?.let {
                    uploadImage(it.drawable)
                }
            }
        }

        val deletePictureBtn = findViewById<Button>(R.id.deletePictureBtn)
        deletePictureBtn.setOnClickListener {
            if (progressBar?.isVisible == false) {
                pictureSelected = false
                deleteImage()
            }
        }

        updatePicture()
    }

    private fun updatePicture() {
        val user = AppSettings.getUser()
        val path = user?.optString("picture_path") ?: ""
        val url = if (path.isNotBlank()) AppSettings.getStorageUrl(path) else null

        pictureDisplay?.let {
            if (url != null) {
                Glide.with(this)
                    .load(url)
                    .into(it)
            } else {
                pictureDisplay?.setImageResource(R.color.transparent)
            }
        }
    }

    private fun previewImage(uri: Uri) {
        pictureDisplay?.let {
            Glide.with(this)
                .load(uri)
                .into(it)
        }
    }

    private fun uploadImage(image: Drawable) {
        progressBar?.visibility = View.VISIBLE

        AuthApiClient.updateProfilePicture(this, image,
            onSuccess = { response ->
                response.optJSONObject("user")?.let {
                    AppSettings.setUser((it))
                    updatePicture()
                }

                statusDisplay?.text = response.optString("message")
                statusDisplay?.setTextColor(getColor(R.color.green))
                progressBar?.visibility = View.INVISIBLE
            },
            onError = { message ->
                statusDisplay?.text = message
                statusDisplay?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            }
        )
    }

    private fun deleteImage() {
        progressBar?.visibility = View.VISIBLE

        AuthApiClient.deleteProfilePicture(this,
            onSuccess = { response ->
                response.optJSONObject("user")?.let {
                    AppSettings.setUser(it)
                    updatePicture()
                }

                statusDisplay?.text = response.optString("message")
                statusDisplay?.setTextColor(getColor(R.color.green))
                progressBar?.visibility = View.INVISIBLE
            },
            onError = { message ->
                statusDisplay?.text = message
                statusDisplay?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            }
        )
    }
}