package com.example.languagetrainingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    val apiKey: Flow<String?> = repository.apiKey
    val selectedModel: Flow<String?> = repository.selectedModel

    private val _availableModels = MutableStateFlow<List<String>>(emptyList())
    val availableModels: StateFlow<List<String>> = _availableModels.asStateFlow()

    init {
        // Set default model if none selected
        viewModelScope.launch {
            selectedModel.collect { model ->
                if (model == null) {
                    selectModel("models/gemini-pro")
                }
            }
        }
    }

    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            repository.saveApiKey(apiKey)
            listModels()
        }
    }

    private fun listModels() {
        // Currently supported Gemini models
        val models = listOf(
            "models/gemini-pro",
            "models/gemini-1.5-pro"
        )
        _availableModels.value = models
    }

    fun selectModel(model: String) {
        viewModelScope.launch {
            repository.saveSelectedModel(model)
        }
    }
}
