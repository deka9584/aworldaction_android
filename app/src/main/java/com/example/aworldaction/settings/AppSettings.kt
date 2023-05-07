package com.example.aworldaction.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class AppSettings : Application() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    fun getPreferences(): SharedPreferences {
        return preferences
    }

    fun getToken(): String? {
        return preferences.getString("token", null)
    }

    fun setToken() {
        val editor = preferences.edit()

        editor?.putString("token", "ciao")
        editor?.apply()
    }
}