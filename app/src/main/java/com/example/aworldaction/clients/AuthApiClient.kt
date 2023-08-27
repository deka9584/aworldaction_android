package com.example.aworldaction.clients

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.requests.VolleyMultipartRequest
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

object AuthApiClient {

    fun register(context: Context, params: HashMap<String, String>, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/signup"

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

    fun logout(context: Context, userToken: String, onSuccess: (JSONObject) -> Unit, onError: (Int) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/logout"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val statusCode = error.networkResponse?.statusCode ?: 0
            Log.e("serverAPI", error.toString())
            onError(statusCode)
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $userToken"
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun loadUser(context: Context, userToken: String, onSuccess: (JSONObject) -> Unit, onError: (Int) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/loggeduser"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val statusCode = error.networkResponse?.statusCode ?: 0
            Log.e("serverAPI", error.toString())
            onError(statusCode)
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $userToken"
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun changePassword(context: Context, params: HashMap<String, String>, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/loggeduser/changepassword"

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
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun updateProfilePicture(context: Context, image: Drawable, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/loggeduser/picture"
        val imageData = AppSettings.getFileDataFromDrawable(image)

        val listener = Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)
            val responseJSON = JSONObject(resultResponse)
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
            val params = HashMap<String, VolleyMultipartRequest.DataPart>()
            params["image"] = VolleyMultipartRequest.DataPart("image.jpg", it, "image/jpeg")
            request.setByteData(params)
        }

        requestQueue.add(request)
    }

    fun deleteProfilePicture(context: Context, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "${AppSettings.getAPIUrl()}/loggeduser/picture"

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
}