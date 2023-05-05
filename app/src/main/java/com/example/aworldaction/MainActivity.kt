package com.example.aworldaction

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private var token: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        token = getSharedPreferences("token", Context.MODE_PRIVATE)

        val userToken: String? = token?.getString("token", null)

        if (userToken == null) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }
}