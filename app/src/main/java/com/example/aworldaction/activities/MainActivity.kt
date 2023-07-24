package com.example.aworldaction.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.auth.LoginActivity
import com.example.aworldaction.activities.auth.RegisterActivity
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

        val loginBtn: Button = findViewById(R.id.loginBtn)
        val registerBtn: Button = findViewById(R.id.registerBtn)

        loginBtn.setOnClickListener {
            if (progressBar?.visibility == View.INVISIBLE) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        registerBtn.setOnClickListener {
            if (progressBar?.visibility == View.INVISIBLE) {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupUser()
    }

    private fun showHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun setupUser() {
        val userToken = AppSettings.getToken()

        if (userToken == null || userToken == "") {
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("user")) {
                val user = responseJSON.getJSONObject("user")

                AppSettings.setUser(user)
                Log.d("user", user.toString())
                showHomeActivity()
            }

            progressBar?.visibility = View.INVISIBLE
        }

        val errorListener = Response.ErrorListener { error ->
            progressBar?.visibility = View.INVISIBLE
            Log.e("serverAPI", error.toString())
            AppSettings.setToken(null)
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