package com.example.aworldaction.requests

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.example.aworldaction.R
import org.json.JSONException
import org.json.JSONObject

object RequestsHelper {
    fun getErrorListener(context: Context, statusDisplay: TextView?, progressBar: ProgressBar?) : Response.ErrorListener {
        return Response.ErrorListener { error ->
            var message = "Error"

            if (error is NoConnectionError) {
                message = context.resources.getString(R.string.server_error)
            } else {
                try {
                    val responseString = String(error.networkResponse?.data ?: byteArrayOf())
                    val jsonResponse = JSONObject(responseString)

                    message =
                        if (jsonResponse.has("message")) jsonResponse.getString("message")
                        else "${error.networkResponse?.statusCode}"
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            Log.e("serverApi", error.toString())
            statusDisplay?.text = message
            statusDisplay?.setTextColor(context.resources.getColor(R.color.red, context.theme))
            progressBar?.visibility = View.INVISIBLE
        }
    }
}