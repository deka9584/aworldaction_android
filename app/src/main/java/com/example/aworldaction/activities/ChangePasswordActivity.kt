package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.clients.AuthApiClient
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var currentPassField: EditText? = null
    private var passField: EditText? = null
    private var passConfirmField: EditText? = null
    private var statusDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        progressBar = findViewById(R.id.progressBar)
        currentPassField = findViewById(R.id.currentPasswordField)
        passField = findViewById(R.id.passwordField)
        passConfirmField = findViewById(R.id.passwordConfirmField)
        statusDisplay = findViewById(R.id.statusDisplay)

        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            val currentPass = currentPassField?.text.toString()
            val pass = passField?.text.toString()
            val passConfirm = passConfirmField?.text.toString()

            if (currentPass.isNotBlank() && pass.isNotBlank() && passConfirm.isNotBlank()) {
                sendChangePassRequest(currentPass, pass, passConfirm)
                resetFields()
            }
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun sendChangePassRequest(currPass: String, pass: String, passConfirm: String) {
        val params = HashMap<String, String>()
        params["current_password"] = currPass
        params["password"] = pass
        params["password_confirmation"] = passConfirm

        progressBar?.visibility = View.VISIBLE

        AuthApiClient.changePassword(this, params,
            onSuccess = { response ->
                response.optJSONObject("user")?.let {
                    AppSettings.setUser(it)
                    Log.d("user", it.toString())
                    finish()
                }

                statusDisplay?.text = response.optString("message")
                statusDisplay?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            },
            onError = { message ->
                statusDisplay?.text = message
                statusDisplay?.setTextColor(getColor(R.color.red))
                progressBar?.visibility = View.INVISIBLE
            }
        )
    }

    private fun resetFields() {
        currentPassField?.text?.clear()
        passField?.text?.clear()
        passConfirmField?.text?.clear()
    }
}