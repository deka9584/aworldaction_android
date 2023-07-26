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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class UploadCampaignPictureActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var pictureDisplay: ImageView? = null
    private var pictureCaption: EditText? = null
    private var picturePlaceholder: LinearLayout? = null
    private var statusDisplay: TextView? = null
    private var imageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_campaign_picture)

        val campaignId = intent.getIntExtra("campaignId", 0)

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
                    uploadImage(it.drawable, campaignId)
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

    private fun uploadImage(image: Drawable, campaignId: Int) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/campaign-pictures"
        val imageData = AppSettings.getFileDataFromDrawable(baseContext, image)

        val listener = Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)
            val result = JSONObject(resultResponse)

            if (result.has("message")) {
                statusDisplay?.text = result.getString("message")
                statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
            }

            if (result.has("campaignPicture")) {
                finish()
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())

            try {
                val jsonResponse = JSONObject(responseString)
                statusDisplay?.text = "${error.networkResponse?.statusCode}"

                if (jsonResponse.has("message")) {
                    val message = jsonResponse.getString("message")
                    statusDisplay?.text = message
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.e("serverApi", error.toString())
            statusDisplay?.setTextColor(resources.getColor(R.color.red, theme))
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
            val dataParams = HashMap<String, VolleyMultipartRequest.DataPart>()
            dataParams["image"] = VolleyMultipartRequest.DataPart("image.jpg", it, "image/jpeg")
            request.setByteData(dataParams)

            val textParams = HashMap<String, String>()
            textParams["campaign_id"] = "$campaignId"
            textParams["caption"] = "${pictureCaption?.text}"
            request.setTextParams(textParams)
        }

        requestQueue.add(request)
        progressBar?.visibility = View.VISIBLE
    }
}