package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
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
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email != "" && password != "") {
                sendPostRequest(email, password)
                // (application as AppSettings).setToken("ciao")
                finish()
            }
        }
    }

    private fun sendPostRequest(email: String, password: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "http://127.0.0.1/api/login"

        val listener = Response.Listener<String> { response ->
            //ss
        }

        val errorListener = Response.ErrorListener { error ->
            //dd
        }

        val request = object : StringRequest(
            Request.Method.POST,
            url, listener, errorListener) {
                override fun getParams(): MutableMap<String, String> {
                    // Parametri della richiesta POST
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            requestQueue.add(request)
    }
}