package com.example.aworldaction

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.aworldaction.settings.AppSettings

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val loginBtn: Button = findViewById(R.id.loginBtn)
        val registerBtn: Button = findViewById(R.id.registerBtn)

        loginBtn.setOnClickListener {
            // L'intent serve a fare comunicare diverse activity
            val intent = Intent(this, LoginActivity::class.java)
            // In questo modo si crea un riferimento all'activty di login e la si avvia
            // con startActivity
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val appSettings = (application as AppSettings).getPreferences()
        val userToken: String? = appSettings?.getString("token", null)

        if (userToken != null && userToken != "") {
            finish()
        }
    }
}