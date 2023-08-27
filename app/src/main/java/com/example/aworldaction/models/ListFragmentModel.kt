package com.example.aworldaction.models

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.clients.ContentApiClient
import com.example.aworldaction.requests.RequestsHelper
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class ListFragmentModel: ViewModel() {

    private val _campaignList = MutableLiveData<List<JSONObject>>()
    val campaignList: LiveData<List<JSONObject>> get() = _campaignList

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun loadList(context: Context, toShow: String?) {
        ContentApiClient.loadCampaignList(context, toShow,
            onSuccess = { response ->
                response.optJSONArray("data")?.let {
                    val list = ArrayList<JSONObject>()
                    for (i in 0 until it.length()) {
                        list.add(it.getJSONObject(i))
                    }
                    _campaignList.value = list
                }

                _message.value = response.optString("message")
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
