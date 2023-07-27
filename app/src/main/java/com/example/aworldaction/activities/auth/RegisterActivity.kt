package com.example.aworldaction.activities.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.MainActivity
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var userField: EditText? = null
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var passwordConfirmField: EditText? = null
    private var authStatus: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressBar = findViewById(R.id.progressBar)
        userField = findViewById(R.id.userField)
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        passwordConfirmField = findViewById(R.id.passwordConfirmField)
        authStatus = findViewById(R.id.authStatus)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        registerBtn.setOnClickListener {
            if (progressBar?.visibility == View.INVISIBLE) {
                val username = userField?.text.toString()
                val email = emailField?.text.toString()
                val password = passwordField?.text.toString()
                val passwordConfirm = passwordConfirmField?.text.toString()

                if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    sendRegisterRequest(username, email, password, passwordConfirm)
                    resetFields()
                }
            }
        }

        val loginLink = findViewById<TextView>(R.id.loginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendRegisterRequest(username: String, email: String, password: String, passwordConfirm: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/signup"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("token")) {
                val token = responseJSON.getString("token")
                AppSettings.setToken(token)
                Log.d("token", token)
            }

            if (responseJSON.has("user")) {
                val user = responseJSON.getJSONObject("user")
                AppSettings.setUser(user)
                finish()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                authStatus?.text = message
                authStatus?.setTextColor(resources.getColor(R.color.green, theme))
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = RequestsHelper.getErrorListener(this, authStatus, progressBar)

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = username
                params["email"] = email
                params["password"] = password
                params["password_confirmation"] = passwordConfirm
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        progressBar?.visibility = View.VISIBLE
        requestQueue.add(request)
    }

    private fun resetFields() {
        userField?.text?.clear()
        emailField?.text?.clear()
        passwordField?.text?.clear()
        passwordConfirmField?.text?.clear()
    }
}