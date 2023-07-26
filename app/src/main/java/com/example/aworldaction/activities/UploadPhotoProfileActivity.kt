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
        if (user?.has("picture_path") == true) {
            val url = AppSettings.getStorageUrl(user.getString("picture_path"))

            pictureDisplay?.let {
                if (url != null) {
                    Glide.with(this)
                        .load(url)
                        .into(it)
                } else {
                    pictureDisplay?.setImageResource(R.drawable.ic_baseline_account_circle_24)
                }
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
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser/picture"
        val imageData = AppSettings.getFileDataFromDrawable(baseContext, image)

        val listener = Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)
            val result = JSONObject(resultResponse)

            if (result.has("message")) {
                statusDisplay?.text = result.getString("message")
                statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
            }

            if (result.has("user")) {
                AppSettings.setUser(result.getJSONObject("user"))
                updatePicture()
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())

            try {
                val response = JSONObject(responseString)

                if (response.has("message")) {
                    val message = response.getString("message")
                    Log.e("serverApi", message)
                    statusDisplay?.text = message
                    statusDisplay?.setTextColor(resources.getColor(R.color.red, theme))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val request = object : VolleyMultipartRequest(
            url,
            listener,
            errorListener
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        imageData?.let {
            val params = HashMap<String, VolleyMultipartRequest.DataPart>()
            params["image"] = VolleyMultipartRequest.DataPart("image.jpg", it, "image/jpeg")
            request.setByteData(params)
        }

        requestQueue.add(request)
        progressBar?.visibility = View.VISIBLE
    }

    private fun deleteImage() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser/picture"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                statusDisplay?.text = message
                statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
            }

            if (responseJSON.has("user")) {
                AppSettings.setUser(responseJSON.getJSONObject("user"))
                updatePicture()
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())

            if (listOf(401, 403, 422).contains(error.networkResponse.statusCode)) {
                val responseString = String(error.networkResponse?.data ?: byteArrayOf())
                try {
                    val jsonResponse = JSONObject(responseString)
                    val errorMessage = jsonResponse.getString("message")
                    statusDisplay?.text = errorMessage
                } catch (e: JSONException) {
                    e.printStackTrace()
                    statusDisplay?.text = resources.getString(R.string.upload_failed)
                }

                statusDisplay?.setTextColor(resources.getColor(R.color.red, theme))
                progressBar?.visibility = View.INVISIBLE
            }
        }

        val request = object : StringRequest(
            Method.DELETE, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
        progressBar?.visibility = View.VISIBLE
    }
}