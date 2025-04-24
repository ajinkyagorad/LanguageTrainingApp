package com.example.languagetrainingapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        private val API_KEY = stringPreferencesKey("api_key")
        private val SELECTED_MODEL = stringPreferencesKey("selected_model")
    }

    val apiKey: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[API_KEY]
    }

    val selectedModel: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[SELECTED_MODEL]
    }

    suspend fun saveApiKey(apiKey: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    suspend fun saveSelectedModel(model: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[SELECTED_MODEL] = model
        }
    }
}
