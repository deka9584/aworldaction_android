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
import com.example.aworldaction.clients.AuthApiClient
import com.example.aworldaction.clients.ContentApiClient
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
                if (userField?.text?.isNotBlank() == true && emailField?.text?.isNotBlank() == true) {
                    sendRegisterRequest()
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

    private fun sendRegisterRequest() {
        val params = HashMap<String, String>()
        params["name"] = "${userField?.text}"
        params["email"] = "${emailField?.text}"
        params["password"] = "${passwordField?.text}"
        params["password_confirmation"] = "${passwordConfirmField?.text}"

        progressBar?.visibility = View.VISIBLE

        AuthApiClient.register(this, params,
            onSuccess = { response ->
                val token = response.optString("token")
                val user = response.optJSONObject("user")

                user?.let {
                    AppSettings.setToken(token)
                    AppSettings.setUser(user)

                    Log.d("usrToken", token)
                    finish()
                }

                authStatus?.text = response.optString("message")
                authStatus?.setTextColor(getColor(R.color.green))
                progressBar?.visibility = View.INVISIBLE
            },
            onError = { message ->
                authStatus?.text = message
                authStatus?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            }
        )
    }

    private fun resetFields() {
        userField?.text?.clear()
        emailField?.text?.clear()
        passwordField?.text?.clear()
        passwordConfirmField?.text?.clear()
    }
}