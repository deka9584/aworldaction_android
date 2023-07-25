package com.example.aworldaction.activities

import VolleyMultipartRequest
import android.app.ProgressDialog
import android.content.ContextParams
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.drawToBitmap
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class UploadPhotoProfileActivity : AppCompatActivity() {
    private var pictureDisplay: ImageView? = null
    private var statusDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo_profile)

        pictureDisplay = findViewById(R.id.pictureDisplay)
        statusDisplay = findViewById(R.id.statusDisplay)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                pictureDisplay?.let {
                    Glide.with(this)
                        .load(uri)
                        .into(it)
                }
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
            pictureDisplay?.let {
                uploadImage(it.drawable)
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
                Log.d("serverApi", result.getString("message"))
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