package com.example.aworldaction.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppSettings.init(this)

        val appSettings = AppSettings.getPreferences()
        val userToken: String? = appSettings?.getString("token", null)

        if (userToken == null) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn?.setOnClickListener {
            AppSettings.removeToken()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }
}