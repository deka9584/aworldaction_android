package com.example.aworldaction.models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aworldaction.clients.ContentApiClient
import org.json.JSONObject

class DetailActivityModel: ViewModel() {

    private val _campaign = MutableLiveData<JSONObject>()
    val campaign: LiveData<JSONObject> get() = _campaign

    private val _pictures = MutableLiveData<List<JSONObject>>()
    val pictures: LiveData<List<JSONObject>> get() = _pictures

    private val _contributors = MutableLiveData<List<JSONObject>>()
    val contributors: LiveData<List<JSONObject>> get() = _contributors

    private val _comments = MutableLiveData<List<JSONObject>>()
    val comments: LiveData<List<JSONObject>> get() = _comments

    fun loadCampaign(context: Context, campaignId: Int) {
        ContentApiClient.loadCampaign(context, campaignId,
            onSuccess = { response ->
                response.optJSONObject("data")?.let { data ->
                    _campaign.value = data

                    data.optJSONArray("pictures")?.let { jsonArray ->
                        _pictures.value = (0 until jsonArray.length()).map {
                            jsonArray.getJSONObject(it)
                        }
                    }

                    data.optJSONArray("contributors")?.let { jsonArray ->
                        _contributors.value = (0 until jsonArray.length()).map {
                            jsonArray.getJSONObject(it)
                        }
                    }

                }
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun loadComments(context: Context, campaignId: Int) {
        ContentApiClient.loadComments(context, campaignId,
            onSuccess = { response ->
                response.optJSONArray("data")?.let { jsonArray ->
                    _comments.value = (0 until jsonArray.length()).map {
                        jsonArray.getJSONObject(it)
                    }
                }
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun sendComment(context:Context, campaignId: Int, comment: String) {
        val params = HashMap<String, String>()
        params["campaign_id"] = "$campaignId"
        params["body"] = comment

        ContentApiClient.postComment(context, params,
            onSuccess = { response ->
                if (response.has("data") || response.has("comment")) {
                    loadComments(context, campaignId)
                }

                if (response.has("message")) {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun updateComment(context: Context, commentId: Int, commentText: String) {
        ContentApiClient.updateComment(context, commentId, commentText,
            onSuccess = { response ->
                if (response.has("data") || response.has("message")) {
                    campaign.value?.optInt("id")?.let {
                        loadComments(context, it)
                    }
                }

                if (response.has("message")) {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun deleteComment(context: Context, commentId: Int) {
        ContentApiClient.deleteComment(context, commentId,
            onSuccess = { response ->
                if (response.has("message")) {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()

                    campaign.value?.optInt("id")?.let {
                        loadComments(context, it)
                    }
                }
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun deletePicture(context: Context, pictureId: Int) {
        ContentApiClient.deletePicture(context, pictureId,
            onSuccess = { response ->
                if (response.has("message")) {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()

                    campaign.value?.optInt("id")?.let {
                        loadCampaign(context, it)
                    }
                }
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}