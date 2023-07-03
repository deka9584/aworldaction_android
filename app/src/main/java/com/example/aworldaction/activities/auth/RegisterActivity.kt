package com.example.aworldaction.activities.auth

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.MainActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private var userField: EditText? = null
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var passwordConfirmField: EditText? = null
    private var authStatus: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userField = findViewById(R.id.userField)
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        passwordConfirmField = findViewById(R.id.passwordConfirmField)
        authStatus = findViewById(R.id.authStatus)

        val backBtn: ImageButton? = findViewById(R.id.backBtn)
        backBtn?.setOnClickListener {
            finish()
        }

        val registerBtn: Button? = findViewById(R.id.registerBtn)
        registerBtn?.setOnClickListener {
            val username = userField?.text.toString()
            val email = emailField?.text.toString()
            val password = passwordField?.text.toString()
            val passwordConfirm = passwordConfirmField?.text.toString()

            if (username != "" && email != "" && password != "" && passwordConfirm != "") {
                sendRegisterRequest(username, email, password, passwordConfirm)
                resetFields()
            }
        }
    }

    private fun sendRegisterRequest(username: String, email: String, password: String, passwordConfirm: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/signup"

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
                this.authStatus?.text = message
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())

            if (error.networkResponse.statusCode == 401) {
                val authStatusText = findViewById<TextView>(R.id.authStatus)
                authStatusText.text = "Invalid email or password"
                authStatusText.setTextColor(Color.parseColor("#FF0000"))
            }
        }

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
        requestQueue.add(request)
    }

    private fun resetFields() {
        userField?.text?.clear()
        emailField?.text?.clear()
        passwordField?.text?.clear()
        passwordConfirmField?.text?.clear()
    }
}