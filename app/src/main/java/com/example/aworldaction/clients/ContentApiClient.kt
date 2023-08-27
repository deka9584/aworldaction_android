package com.example.aworldaction.clients

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.requests.VolleyMultipartRequest
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

object ContentApiClient {

    private fun getAPIErrorMessage(error: VolleyError): String {
        if (error is NoConnectionError) {
            return "Unable to reach the server"
        }

        try {
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())
            val jsonResponse = JSONObject(responseString)

            return "${jsonResponse.optString("message", "Error")} (${error.networkResponse?.statusCode})"
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
            val message = getAPIErrorMessage(error)
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

    fun loadCampaign(context: Context, campaignId: Int, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/campaigns/$campaignId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun postCampaign(context: Context, params: HashMap<String, String>, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/campaigns"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
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
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun loadComments(context: Context, campaignId: Int, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/comments/filter/${campaignId}"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun postComment(context: Context, params: HashMap<String, String>, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/comments"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
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
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun updateComment(context: Context, commentId: Int, commentText: String, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/comments/$commentId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.PUT, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["body"] = commentText
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun deleteComment(context: Context, commentId: Int, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/comments/$commentId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.DELETE, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun postPicture(context: Context, params: HashMap<String, String>, image: Drawable, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/campaign-pictures"
        val imageData = AppSettings.getFileDataFromDrawable(image)

        val listener = Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)
            val responseJSON = JSONObject(resultResponse)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : VolleyMultipartRequest(
            url,
            listener,
            errorListener
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        imageData?.let {
            val dataParams = HashMap<String, VolleyMultipartRequest.DataPart>()
            dataParams["image"] = VolleyMultipartRequest.DataPart("image.jpg", it, "image/jpeg")
            request.setByteData(dataParams)
        }

        request.setTextParams(params)
        requestQueue.add(request)
    }

    fun deletePicture(context: Context, pictureId: Int, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/campaign-pictures/$pictureId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.DELETE, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun toggleCampaignFavourite(context: Context, campaignId: Int, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/relate-campaign/logged"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
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
