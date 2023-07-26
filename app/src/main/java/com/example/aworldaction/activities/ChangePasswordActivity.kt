package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var currentPassField: EditText? = null
    private var passField: EditText? = null
    private var passConfirmField: EditText? = null
    private var statusDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        progressBar = findViewById(R.id.progressBar)
        currentPassField = findViewById(R.id.currentPasswordField)
        passField = findViewById(R.id.passwordField)
        passConfirmField = findViewById(R.id.passwordConfirmField)
        statusDisplay = findViewById(R.id.statusDisplay)

        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            if (currentPassField?.text?.isBlank() == false && passField?.text?.isBlank() == false) {
                sendChangePassRequest()
                resetFields()
            }
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun sendChangePassRequest() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser/changepassword"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("user")) {
                val user = responseJSON.getJSONObject("user")
                AppSettings.setUser(user)
                Log.d("user", user.toString())
                finish()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                statusDisplay?.text = message
                statusDisplay?.setTextColor(resources.getColor(R.color.green, theme))
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
                    statusDisplay?.text = "${error.networkResponse?.statusCode}"
                }

                statusDisplay?.setTextColor(resources.getColor(R.color.red, theme))
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["current_password"] = "${currentPassField?.text}"
                params["password"] = "${passField?.text}"
                params["password_confirmation"] = "${passConfirmField?.text}"
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

    private fun resetFields() {
        currentPassField?.text?.clear()
        passField?.text?.clear()
        passConfirmField?.text?.clear()
    }
}