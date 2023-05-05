package com.example.aworldaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

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

        }
    }
}