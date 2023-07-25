package com.example.aworldaction.settings

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


object AppSettings {
    private val apiUrl = URL("https://aworldaction.zapto.org/api")
    private var preferences: SharedPreferences? = null
    private var user: JSONObject? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    fun getAPIUrl(): URL {
        return apiUrl
    }

    fun getPreferences(): SharedPreferences? {
        return preferences
    }

    fun getToken(): String? {
        return preferences?.getString("token", null)
    }

    fun setToken(token: String?) {
        val editor = preferences?.edit()

        editor?.putString("token", token)
        editor?.apply()
    }

    fun removeToken() {
        val editor = preferences?.edit()

        editor?.remove("token")
        editor?.apply()
    }

    fun getUser(): JSONObject? {
        return user
    }

    fun setUser(user: JSONObject?) {
        this.user = user
    }

    fun getStorageUrl(path: String): URL? {
        val serverUrl = apiUrl.toString().replace("api", "storage")
        val newUrl = path.replace("public", serverUrl)

        try {
            return URL(newUrl)
        } catch (error: MalformedURLException) {
            error.printStackTrace()
        }

        return null
    }

    fun formatDateString(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        try {
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getFileDataFromDrawable(context: Context?, drawable: Drawable): ByteArray? {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}