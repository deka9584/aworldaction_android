package com.example.aworldaction.managers

import android.util.Log
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.ListFragment
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class ListFragmentManager(private val fragment: ListFragment) {
    private val campaignList = ArrayList<JSONObject>()

    fun loadList(toShow: String?) {
        val requestQueue = Volley.newRequestQueue(fragment.context)
        val api = AppSettings.getAPIUrl().toString()
        val url = when (toShow) {
            "inprogress" -> "$api/inprogress"
            "favourites" -> "$api/favourites"
            "completed" -> "$api/completed"
            else -> return
        }

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data")) {
                val campaigns = responseJSON.getJSONArray("data")

                campaignList.clear()
                for (i in 0 until campaigns.length()) {
                    campaignList.add(campaigns.getJSONObject(i))
                }

                Log.d("context", fragment.context.toString())
                fragment.displayList(campaignList)
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
                headers["Authorization"] = "Bearer " + AppSettings.getToken()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun getCampaignList(): ArrayList<JSONObject> {
        return campaignList
    }
}