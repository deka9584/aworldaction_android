package com.example.aworldaction.activities.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.example.aworldaction.R
import com.example.aworldaction.clients.AuthApiClient
import com.example.aworldaction.settings.AppSettings

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

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            if (progressBar?.visibility == View.INVISIBLE) {
                val email = emailField?.text.toString()
                val password = passwordField?.text.toString()

                if (email.isNotBlank() && password.isNotBlank()) {
                    sendLoginRequest(email, password)
                    resetFields()
                }
            }
        }

        val newAccountLink = findViewById<TextView>(R.id.newAccountLink)
        newAccountLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendLoginRequest(email: String, password: String) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        progressBar?.visibility = View.VISIBLE

        AuthApiClient.login(this, params,
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
        emailField?.text?.clear()
        passwordField?.text?.clear()
    }
}