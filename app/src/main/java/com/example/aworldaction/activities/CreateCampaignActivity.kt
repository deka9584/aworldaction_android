package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class CreateCampaignActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var titleField: EditText? = null
    private var captionField: EditText? = null
    private var serverMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)

        progressBar = findViewById(R.id.progressBar)
        titleField = findViewById(R.id.titleField)
        captionField = findViewById(R.id.captionField)
        serverMessage = findViewById(R.id.authStatus)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn?.setOnClickListener {
            if (titleField?.text?.isBlank() == false && captionField?.text?.isBlank() == false) {
                storeCampaign()
            }
        }
    }

    private fun storeCampaign() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/campaigns"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("campaign") || responseJSON.has("data")) {
                finish()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                serverMessage?.text = message
                serverMessage?.setTextColor(resources.getColor(R.color.green, theme))
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())
            Log.e("serverAPI", error.toString())

            try {
                val jsonResponse = JSONObject(responseString)
                val errorMessage = jsonResponse.getString("message")
                serverMessage?.text = errorMessage
            } catch (e: JSONException) {
                e.printStackTrace()
                serverMessage?.text = "${error.networkResponse.statusCode}"
            }

            serverMessage?.setTextColor(resources.getColor(R.color.red, theme))
            progressBar?.visibility = View.INVISIBLE
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = titleField?.text.toString()
                params["description"] = captionField?.text.toString()
                params["location_name"] = "TO DO"
                params["location_lat"] = "0.0"
                params["location_lng"] = "0.0"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        progressBar?.visibility = View.VISIBLE
        requestQueue.add(request)
    }
}