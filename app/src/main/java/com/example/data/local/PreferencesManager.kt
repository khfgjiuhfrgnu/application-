package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("khaled_ai_prefs", Context.MODE_PRIVATE)

    private val _apiKeyFlow = MutableStateFlow(getApiKey())
    val apiKeyFlow: StateFlow<String> = _apiKeyFlow.asStateFlow()

    private val _selectedModelFlow = MutableStateFlow(getSelectedModel())
    val selectedModelFlow: StateFlow<String> = _selectedModelFlow.asStateFlow()

    private val _defaultLanguageFlow = MutableStateFlow(getDefaultLanguage())
    val defaultLanguageFlow: StateFlow<String> = _defaultLanguageFlow.asStateFlow()

    fun getApiKey(): String {
        val customKey = prefs.getString(KEY_API_KEY, "") ?: ""
        if (customKey.isNotBlank()) return customKey
        // Fallback to BuildConfig if present and not placeholder
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }
        return if (buildKey.isNotBlank() && !buildKey.contains("MY_GEMINI_API_KEY")) buildKey else ""
    }

    fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key.trim()).apply()
        _apiKeyFlow.value = getApiKey()
    }

    fun getSelectedModel(): String {
        return prefs.getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    fun setSelectedModel(model: String) {
        prefs.edit().putString(KEY_MODEL, model).apply()
        _selectedModelFlow.value = model
    }

    fun getDefaultLanguage(): String {
        return prefs.getString(KEY_LANG, "en") ?: "en"
    }

    fun setDefaultLanguage(lang: String) {
        prefs.edit().putString(KEY_LANG, lang).apply()
        _defaultLanguageFlow.value = lang
    }

    fun getTtsSpeed(): Float = prefs.getFloat(KEY_TTS_SPEED, 1.0f)
    fun setTtsSpeed(speed: Float) = prefs.edit().putFloat(KEY_TTS_SPEED, speed).apply()

    fun getTtsPitch(): Float = prefs.getFloat(KEY_TTS_PITCH, 1.0f)
    fun setTtsPitch(pitch: Float) = prefs.edit().putFloat(KEY_TTS_PITCH, pitch).apply()

    companion object {
        private const val KEY_API_KEY = "api_key"
        private const val KEY_MODEL = "selected_model"
        private const val KEY_LANG = "default_language"
        private const val KEY_TTS_SPEED = "tts_speed"
        private const val KEY_TTS_PITCH = "tts_pitch"
        const val DEFAULT_MODEL = "gemini-2.5-flash"
    }
}
