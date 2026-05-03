package com.turkcell.libraryapp.data.internal

import android.content.Context
import android.content.SharedPreferences



class ThemePref(context: Context)
{
    private val prefs: SharedPreferences = context.getSharedPreferences("ayarlar", Context.MODE_PRIVATE)

    suspend fun saveTheme(lightMode: Boolean)
    {
        prefs
            .edit()
            .putBoolean("light_mode", lightMode)
            .apply();
    }

    fun getTheme(): Boolean = prefs.getBoolean("light_mode", true)
}