package com.example.aworldaction.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.net.URL

object AppSettings {
    private val apiUrl = URL("https://aworldaction.zapto.org/api")
    private lateinit var preferences: SharedPreferences
    private var user: JSONObject? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    fun getAPIUrl(): URL {
        return apiUrl
    }

    fun getPreferences(): SharedPreferences {
        return preferences
    }

    fun getToken(): String? {
        return preferences.getString("token", null)
    }

    fun setToken(token: String?) {
        val editor = preferences.edit()

        editor.putString("token", token)
        editor.apply()
    }

    fun removeToken() {
        val editor = preferences.edit()

        editor.remove("token")
        editor.apply()
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
        return URL(newUrl)
    }
}