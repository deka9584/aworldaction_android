package com.example.aworldaction.activities

import android.app.ProgressDialog
import android.graphics.Bitmap
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


class UploadPhotoProfileActivity : AppCompatActivity() {
    private var pictureDisplay: ImageView? = null
    private var statusDisplay: TextView? = null
    private var imageUri: Uri? = null

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
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(it)
                }

                imageUri = uri
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
            uploadImage()
        }
    }

    fun getStringImage(bmp: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun uploadImage() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser/picture"
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val image: String = getStringImage(bitmap)!!

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("user")) {
                val user = responseJSON.getJSONObject("user")
                AppSettings.setUser(user)
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                statusDisplay?.text = message
                statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())

            val responseString = String(error.networkResponse?.data ?: byteArrayOf())

            try {
                val jsonResponse = JSONObject(responseString)
                val errorMessage = jsonResponse.getString("message")
                statusDisplay?.text = errorMessage
            } catch (e: JSONException) {
                e.printStackTrace()
                statusDisplay?.text = resources.getString(R.string.registration_failed)
            }

            statusDisplay?.setTextColor(resources.getColor(R.color.red, theme))
        }

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            listener,
            errorListener
        ) {
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["image"] = image
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }
}