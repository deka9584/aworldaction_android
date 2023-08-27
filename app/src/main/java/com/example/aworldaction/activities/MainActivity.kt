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
    private var authOptionsView: LinearLayout? = null
    private var connectionErrorView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppSettings.init(this)

        progressBar = findViewById(R.id.progressBar)
        authOptionsView = findViewById(R.id.authOptionsView)
        connectionErrorView = findViewById(R.id.connectionErrorView)

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val retryBtn = findViewById<Button>(R.id.retryBtn)

        loginBtn?.setOnClickListener {
            if (progressBar?.isVisible == false) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        registerBtn?.setOnClickListener {
            if (progressBar?.isVisible == false) {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

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
        authOptionsView?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun showConnectionError(show: Boolean) {
        progressBar?.visibility = View.GONE
        connectionErrorView?.visibility = if (show) View.VISIBLE else View.INVISIBLE
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

                progressBar?.visibility = View.VISIBLE
            },
            onError = { statusCode ->
                if (statusCode == 0) {
                    showConnectionError(true)
                } else {
                    AppSettings.setToken(null)
                    showAuthOptions(true)
                }

                progressBar?.visibility = View.VISIBLE
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("user")) {
                val user = responseJSON.getJSONObject("user")

                AppSettings.setUser(user)
                Log.d("user", user.toString())
                launchHomeActivity()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            if (error is NoConnectionError) {
                showConnectionError(true)
            } else {
                AppSettings.setToken(null)
                showAuthOptions(true)
            }

            Log.e("serverAPI", error.toString())
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $userToken"
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        progressBar?.visibility = View.VISIBLE
        requestQueue.add(request)
    }
}