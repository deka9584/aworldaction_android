package com.example.aworldaction.activities.auth

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.MainActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var authStatus: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = findViewById(R.id.progressBar)
        authStatus = findViewById(R.id.authStatus)
        emailField = findViewById(R.id.userField)
        passwordField = findViewById(R.id.passwordField)

        val backBtn: ImageButton? = findViewById(R.id.backBtn)
        backBtn?.setOnClickListener {
            finish()
        }

        val loginBtn: Button? = findViewById(R.id.loginBtn)
        loginBtn?.setOnClickListener {
            if (progressBar?.visibility == View.INVISIBLE) {
                val email = emailField?.text.toString()
                val password = passwordField?.text.toString()

                if (email != "" && password != "") {
                    sendLoginRequest(email, password)
                    resetFields()
                }
            }
        }
    }

    private fun sendLoginRequest(email: String, password: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/login"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("token")) {
                val token = responseJSON.getString("token")
                val user = responseJSON.getJSONObject("user")

                AppSettings.setToken(token)
                AppSettings.setUser(user)

                Log.d("token", token)
                Log.d("user", user.toString())

                finish()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")
                authStatus?.text = message
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())

            if (listOf(401, 403, 422).contains(error.networkResponse.statusCode)) {
                authStatus?.text = resources.getString(R.string.login_failed)
                authStatus?.setTextColor(Color.parseColor("#FF0000"))
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
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
        emailField?.text?.clear()
        passwordField?.text?.clear()
    }
}