package com.example.aworldaction

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.aworldaction.settings.AppSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appSettings = (application as AppSettings).getPreferences()
        val userToken: String? = appSettings?.getString("token", null)

        if (userToken == null) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn?.setOnClickListener {
            val editor = appSettings?.edit()
            editor?.remove("token")
            editor?.apply()

            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }
}