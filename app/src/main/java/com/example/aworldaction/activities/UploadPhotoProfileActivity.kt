package com.example.aworldaction.activities

import VolleyMultipartRequest
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject


class UploadPhotoProfileActivity : AppCompatActivity() {
    private var pictureDisplay: ImageView? = null
    private var statusDisplay: TextView? = null
    private var pictureSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo_profile)

        pictureDisplay = findViewById(R.id.pictureDisplay)
        statusDisplay = findViewById(R.id.statusDisplay)

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

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                pictureDisplay?.let {
                    Glide.with(this)
                        .load(uri)
                        .into(it)
                }

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
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val uploadBtn = findViewById<Button>(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            if (pictureSelected) {
                pictureDisplay?.let {
                    uploadImage(it.drawable)
                }
            }
        }
    }

    private fun uploadImage(image: Drawable) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser/picture"
        val params = HashMap<String, VolleyMultipartRequest.DataPart>()
        val imageData = AppSettings.getFileDataFromDrawable(baseContext, image)

        val listener = Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)

            try {
                val result = JSONObject(resultResponse)

                if (result.has("message")) {
                    statusDisplay?.text = result.getString("message")
                    statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
                }

                if (result.has("user")) {
                    AppSettings.setUser(result.getJSONObject("user"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            val networkResponse = error.networkResponse

            networkResponse?.let {
                val result = String(networkResponse.data)

                try {
                    val response = JSONObject(result)
                    Log.e("serverApi", response.getString("message"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
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
            params["image"] = VolleyMultipartRequest.DataPart("image.jpg", it, "image/jpeg")
            request.setByteData(params)
        }

        requestQueue.add(request)
    }

}