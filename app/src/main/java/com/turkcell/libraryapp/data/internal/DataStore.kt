package com.turkcell.libraryapp.data.internal

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map




private val Context.dataStore by preferencesDataStore(name="ayarlar")

class ThemePref2(private val context: Context) {

    private val keyLightMode = booleanPreferencesKey("light_mode")

    // collectAsState()
    val lightThemeFlow: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[keyLightMode] ?: true}

    suspend fun saveTheme(lightMode: Boolean)
    {
        context.dataStore.edit {prefs -> prefs[keyLightMode] = lightMode }
    }
}
// androidx.datastore:datastore-preferences:1.1.1