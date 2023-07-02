package com.example.aworldaction.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.auth.WelcomeActivity
import com.example.aworldaction.activities.fragments.AccountFragment
import com.example.aworldaction.activities.fragments.ListFragment
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppSettings.init(this)

        if (AppSettings.getToken() == null) {
            showWelcomeView()
        } else {
            checkAuth()
        }
    }

    private fun checkAuth() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = AppSettings.getAPIUrl().toString() + "/loggeduser"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("user")) {
                val user = responseJSON.getJSONObject("user")
                AppSettings.setUser(user)
                Log.d("user", user.toString())
                showCampaignList()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())

            AppSettings.setToken(null)
            showWelcomeView()
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + AppSettings.getToken()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun showCampaignList() {
        val inprogressBtn: LinearLayout? = findViewById(R.id.inprogressBtn)
        val favouritesBtn: LinearLayout? = findViewById(R.id.favouritesBtn)
        val completedBtn: LinearLayout? = findViewById(R.id.completedBtn)
        val accountBtn: LinearLayout? = findViewById(R.id.accountBtn)

        inprogressBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ListFragment.newInstance("inprogress"))
                .commit()
        }

        favouritesBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ListFragment.newInstance("favourites"))
                .commit()
        }

        completedBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ListFragment.newInstance("completed"))
                .commit()
        }

        accountBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, AccountFragment())
                .commit()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, ListFragment.newInstance("inprogress"))
            .commit()
    }

    fun showWelcomeView() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }
}