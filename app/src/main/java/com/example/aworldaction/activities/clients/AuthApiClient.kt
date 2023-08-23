package com.example.aworldaction.activities.clients

import android.content.Context
import android.util.Log
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

object AuthApiClient {

    fun login(context: Context, params: HashMap<String, String>, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/login"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            var message = "Error ${error.networkResponse?.statusCode}"

            if (error is NoConnectionError) {
                message = context.getString(R.string.server_error)
            } else {
                try {
                    val responseString = String(error.networkResponse.data ?: byteArrayOf())
                    val jsonResponse = JSONObject(responseString)

                    if (jsonResponse.has("message")) {
                        message = jsonResponse.getString("message")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }
}