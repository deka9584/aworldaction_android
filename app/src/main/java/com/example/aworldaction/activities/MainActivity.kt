package com.example.aworldaction.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.auth.LoginActivity
import com.example.aworldaction.activities.auth.RegisterActivity
import com.example.aworldaction.clients.AuthApiClient
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppSettings.init(this)

        progressBar = findViewById(R.id.progressBar)

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn?.setOnClickListener {
            if (progressBar?.isVisible == false) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        registerBtn?.setOnClickListener {
            if (progressBar?.isVisible == false) {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        val retryBtn = findViewById<Button>(R.id.retryBtn)
        retryBtn.setOnClickListener {
            if (progressBar?.isVisible == false) {
                showConnectionError(false)
                setupUser()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupUser()
    }

    private fun launchHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun showAuthOptions(show: Boolean) {
        progressBar?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.authOptionsView)?.visibility =
            if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun showConnectionError(show: Boolean) {
        progressBar?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.connectionErrorView)?.visibility =
            if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun setupUser() {
        val userToken = AppSettings.getToken()

        if (userToken == null || userToken.isBlank()) {
            showAuthOptions(true)
            return
        }

        showAuthOptions(false)
        progressBar?.visibility = View.VISIBLE

        AuthApiClient.loadUser(this, userToken,
            onSuccess = { response ->
                response.optJSONObject("user")?.let {
                    AppSettings.setUser(it)
                    Log.d("user", it.toString())
                    launchHomeActivity()
                }

                progressBar?.visibility = View.GONE
            },
            onError = { statusCode ->
                if (statusCode == 0) {
                    showConnectionError(true)
                } else {
                    AppSettings.setToken(null)
                    showAuthOptions(true)
                }

                progressBar?.visibility = View.GONE
            }
        )
    }
}