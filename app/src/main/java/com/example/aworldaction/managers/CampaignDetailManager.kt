package com.example.aworldaction.managers

import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class CampaignDetailManager(private val activity: DetailActivity) {
    private var campaign: JSONObject? = null
    private val pictures = ArrayList<JSONObject>()
    private val contributors = ArrayList<JSONObject>()
    private val comments = ArrayList<JSONObject>()

    fun loadCampaign(campaignId: Int) {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = AppSettings.getAPIUrl().toString() + "/campaigns/$campaignId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data")) {
                val campaign = responseJSON.getJSONObject("data")

                if (campaign.has("pictures")) {
                    val pictures = campaign.getJSONArray("pictures")

                    this.pictures.clear()
                    for (i in 0 until pictures.length()) {
                        this.pictures.add(pictures.getJSONObject(i))
                    }
                }

                if (campaign.has("contributors")) {
                    val contributors = campaign.getJSONArray("contributors")

                    this.contributors.clear()
                    for (i in 0 until contributors.length()) {
                        this.contributors.add(contributors.getJSONObject(i))
                    }
                }

                this.campaign = campaign
                activity.showCampaignData()
                loadComments()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
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

    fun loadComments() {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = AppSettings.getAPIUrl().toString() + "/comments/filter/${campaign?.getInt("id")}"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data")) {
                val data = responseJSON.getJSONArray("data")

                comments.clear()
                for (i in 0 until data.length()) {
                    comments.add(data.getJSONObject(i))
                }
                activity.showComments()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
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

    fun sendComment(comment: String) {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = AppSettings.getAPIUrl().toString() + "/comments"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data") || responseJSON.has("comment")) {
                loadComments()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")

                Log.d("serverApi", message)
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                loadComments()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["campaign_id"] = "${campaign?.getInt("id")}"
                params["body"] = comment
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

    fun updateComment(commentId: Int, commentText: String) {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = AppSettings.getAPIUrl().toString() + "/comments/$commentId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data") || responseJSON.has("comment")) {
                loadComments()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")

                Log.d("serverApi", message)
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
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

    fun deleteComment(commentId: Int) {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = AppSettings.getAPIUrl().toString() + "/comments/$commentId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")

                Log.d("serverApi", message)
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                loadComments()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
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

    fun deletePicture(pictureId: Int) {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = AppSettings.getAPIUrl().toString() + "/campaign-pictures/$pictureId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")

                Log.d("serverApi", message)
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                campaign?.let {
                    if (it.has("id")) {
                        loadCampaign(it.getInt("id"))
                    }
                }
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
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

    fun getCampaign(): JSONObject? {
        return campaign
    }

    fun getPictures(): ArrayList<JSONObject> {
        return pictures
    }

    fun getContributors(): ArrayList<JSONObject> {
        return contributors
    }

    fun getComments(): ArrayList<JSONObject> {
        return comments
    }
}