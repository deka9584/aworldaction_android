package com.example.aworldaction.activities.clients

import android.content.Context
import android.util.Log
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

object ContentApiClient {

    private fun getAPIErrorMessage(error: VolleyError): String {
        try {
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())
            val jsonResponse = JSONObject(responseString)

            if (jsonResponse.has("message")) {
                return jsonResponse.getString("message")
            }

            return "Error ${error.networkResponse?.statusCode}"
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return "Unknown error"
    }

    fun loadCampaignList(context: Context, toShow: String?, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val apiUrl = "${AppSettings.getAPIUrl()}"
        val url = when (toShow) {
            "inprogress" -> "${apiUrl}/inprogress"
            "favourites" -> "${apiUrl}/favourites"
            "completed" -> "${apiUrl}/completed"
            else -> return
        }

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message =
                if (error is NoConnectionError) context.getString(R.string.server_error)
                else getAPIErrorMessage(error)

            onError(message)
            Log.e("serverApi", error.toString())
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

    fun toggleCampaignFavourite(context: Context, campaignId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/relate-campaign/logged"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("message")) {
                onSuccess(responseJSON.getString("message"))
            }
        }

        val errorListener = Response.ErrorListener { error ->
            val message =
                if (error is NoConnectionError) context.getString(R.string.server_error)
                else getAPIErrorMessage(error)

            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["campaign_id"] = "$campaignId"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + AppSettings.getToken()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }
}
