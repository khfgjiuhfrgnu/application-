package com.example.service.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import java.util.UUID

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _waveformAmplitudes = MutableStateFlow(List(24) { 0.15f })
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    private val _availableLocales = MutableStateFlow<List<String>>(emptyList())
    val availableLocales: StateFlow<List<String>> = _availableLocales.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var visualizerJob: Job? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                    startVisualizerAnimation()
                }

                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                    stopVisualizerAnimation()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                    stopVisualizerAnimation()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isPlaying.value = false
                    stopVisualizerAnimation()
                }
            })

            // Collect available locales
            val locales = tts?.availableLanguages?.mapNotNull { it.language }?.distinct() ?: emptyList()
            _availableLocales.value = locales
        }
    }

    fun speak(
        text: String,
        languageCode: String = "en",
        pitch: Float = 1.0f,
        speed: Float = 1.0f
    ) {
        if (!isInitialized || tts == null) return

        tts?.stop()

        val locale = when (languageCode) {
            "ar", "ary" -> Locale("ar")
            "fr" -> Locale.FRENCH
            else -> Locale.US
        }

        tts?.language = locale
        tts?.setPitch(pitch.coerceIn(0.5f, 2.0f))
        tts?.setSpeechRate(speed.coerceIn(0.5f, 2.0f))

        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
        stopVisualizerAnimation()
    }

    fun synthesizeToFile(
        text: String,
        languageCode: String,
        pitch: Float,
        speed: Float,
        onComplete: (File?) -> Unit
    ) {
        if (!isInitialized || tts == null) {
            onComplete(null)
            return
        }

        val locale = when (languageCode) {
            "ar", "ary" -> Locale("ar")
            "fr" -> Locale.FRENCH
            else -> Locale.US
        }
        tts?.language = locale
        tts?.setPitch(pitch)
        tts?.setSpeechRate(speed)

        val outputDir = File(context.filesDir, "audio_creations").apply { if (!exists()) mkdirs() }
        val outputFile = File(outputDir, "khaled_speech_${System.currentTimeMillis()}.wav")

        val utteranceId = "synth_${System.currentTimeMillis()}"
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            override fun onDone(id: String?) {
                if (id == utteranceId) onComplete(outputFile)
            }
            override fun onError(id: String?) {
                if (id == utteranceId) onComplete(null)
            }
        })

        tts?.synthesizeToFile(text, params, outputFile, utteranceId)
    }

    private fun startVisualizerAnimation() {
        visualizerJob?.cancel()
        visualizerJob = coroutineScope.launch {
            while (isActive && _isPlaying.value) {
                // Generate dynamic energetic bar heights
                _waveformAmplitudes.value = List(24) {
                    val base = (0.2f + Math.random().toFloat() * 0.75f)
                    base
                }
                delay(60)
            }
        }
    }

    private fun stopVisualizerAnimation() {
        visualizerJob?.cancel()
        _waveformAmplitudes.value = List(24) { 0.15f }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
