package com.example.aworldaction.requests

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Response
import com.example.aworldaction.R
import org.json.JSONException
import org.json.JSONObject

object RequestsHelper {
    fun getErrorListener(context: Context, statusDisplay: TextView?, progressBar: ProgressBar?) : Response.ErrorListener {
        return Response.ErrorListener { error ->
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())

            try {
                val jsonResponse = JSONObject(responseString)

                if (jsonResponse.has("message")) {
                    val message = jsonResponse.getString("message")
                    statusDisplay?.text = message
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (statusDisplay?.text?.isBlank() == true) {
                statusDisplay.text = "${error.networkResponse?.statusCode}"
            }

            Log.e("serverApi", error.toString())
            statusDisplay?.setTextColor(context.resources.getColor(R.color.red, context.theme))
            progressBar?.visibility = View.INVISIBLE
        }
    }
}