package com.example.aworldaction.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.net.URL

object AppSettings {
    private val apiUrl = URL("http://172.16.2.43:8000/api")
    private lateinit var preferences: SharedPreferences

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

    fun setToken(token: String) {
        val editor = preferences.edit()

        editor.putString("token", token)
        editor.apply()
    }

    fun removeToken() {
        val editor = preferences.edit()

        editor.remove("token")
        editor.apply()
    }
}