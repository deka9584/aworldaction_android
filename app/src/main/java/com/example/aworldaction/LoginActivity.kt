package com.example.aworldaction

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.aworldaction.settings.AppSettings

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val emailField: EditText = findViewById(R.id.userField)
        val passwordField: EditText = findViewById(R.id.passwordField)
        val loginBtn: Button = findViewById(R.id.loginBtn)

        loginBtn.setOnClickListener {
            if (emailField.text.toString() != "" && passwordField.text.toString() != "") {
                val appSettings = (application as AppSettings).getPreferences()
                val editor = appSettings?.edit()

                editor?.putString("token", "ciao")
                editor?.apply()

                finish()
            }
        }
    }
}