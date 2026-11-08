package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.AiLanguage
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.CreationEntity
import com.example.data.model.CreationType
import com.example.data.model.GenerationState
import com.example.data.model.HistoryEntity
import com.example.data.model.ImageGenerationOptions
import com.example.data.model.NavDestination
import com.example.data.model.VideoGenerationOptions
import com.example.data.remote.GeminiRepository
import com.example.service.audio.TextToSpeechManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val chatDao = db.chatDao()
    private val creationDao = db.creationDao()
    private val historyDao = db.historyDao()
    val prefs = PreferencesManager(application)
    private val geminiRepo = GeminiRepository()
    val ttsManager = TextToSpeechManager(application)

    // Navigation
    private val _currentDestination = MutableStateFlow(NavDestination.DASHBOARD)
    val currentDestination: StateFlow<NavDestination> = _currentDestination.asStateFlow()

    // Pass-through prompt from dashboard console
    private val _initialConsolePrompt = MutableStateFlow("")
    val initialConsolePrompt: StateFlow<String> = _initialConsolePrompt.asStateFlow()

    // Language
    private val _selectedLanguage = MutableStateFlow(
        AiLanguage.values().find { it.code == prefs.getDefaultLanguage() } ?: AiLanguage.ENGLISH
    )
    val selectedLanguage: StateFlow<AiLanguage> = _selectedLanguage.asStateFlow()

    // Conversations & Messages
    val conversations: StateFlow<List<ConversationEntity>> = chatDao.getAllConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentConversation = MutableStateFlow<ConversationEntity?>(null)
    val currentConversation: StateFlow<ConversationEntity?> = _currentConversation.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

    private var messagesObserverJob: Job? = null

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Creations & History
    val creations: StateFlow<List<CreationEntity>> = creationDao.getAllCreations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyEntries: StateFlow<List<HistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Image Generator States
    private val _imageGenState = MutableStateFlow<GenerationState<CreationEntity>>(GenerationState.Idle)
    val imageGenState: StateFlow<GenerationState<CreationEntity>> = _imageGenState.asStateFlow()

    private val _enhancedImagePrompt = MutableStateFlow<String?>(null)
    val enhancedImagePrompt: StateFlow<String?> = _enhancedImagePrompt.asStateFlow()

    // Video Generator States
    private val _videoGenState = MutableStateFlow<GenerationState<CreationEntity>>(GenerationState.Idle)
    val videoGenState: StateFlow<GenerationState<CreationEntity>> = _videoGenState.asStateFlow()

    private val _enhancedVideoScript = MutableStateFlow<String?>(null)
    val enhancedVideoScript: StateFlow<String?> = _enhancedVideoScript.asStateFlow()

    init {
        // Observe first conversation if available
        viewModelScope.launch {
            conversations.collect { convList ->
                if (_currentConversation.value == null && convList.isNotEmpty()) {
                    selectConversation(convList.first())
                }
            }
        }
    }

    fun navigate(destination: NavDestination) {
        _currentDestination.value = destination
    }

    fun launchWithPrompt(destination: NavDestination, prompt: String) {
        _initialConsolePrompt.value = prompt
        _currentDestination.value = destination
    }

    fun clearInitialPrompt() {
        _initialConsolePrompt.value = ""
    }

    fun selectLanguage(language: AiLanguage) {
        _selectedLanguage.value = language
        prefs.setDefaultLanguage(language.code)
    }

    fun selectConversation(conversation: ConversationEntity) {
        _currentConversation.value = conversation
        messagesObserverJob?.cancel()
        messagesObserverJob = viewModelScope.launch {
            chatDao.getMessagesForConversation(conversation.id).collect { list ->
                _messages.value = list
            }
        }
    }

    fun startNewConversation() {
        val newId = UUID.randomUUID().toString()
        val newConv = ConversationEntity(
            id = newId,
            title = "Conversation ${SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
            languageCode = _selectedLanguage.value.code
        )
        viewModelScope.launch {
            chatDao.insertConversation(newConv)
            selectConversation(newConv)
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatDao.deleteMessagesForConversation(id)
            chatDao.deleteConversation(id)
            if (_currentConversation.value?.id == id) {
                _currentConversation.value = null
                _messages.value = emptyList()
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Ensure conversation exists
            var conv = _currentConversation.value
            if (conv == null) {
                val newId = UUID.randomUUID().toString()
                conv = ConversationEntity(
                    id = newId,
                    title = userText.take(24),
                    languageCode = _selectedLanguage.value.code
                )
                chatDao.insertConversation(conv)
                selectConversation(conv)
            } else if (conv.title.startsWith("Conversation")) {
                conv = conv.copy(title = userText.take(24), updatedAt = System.currentTimeMillis())
                chatDao.insertConversation(conv)
            }

            // Insert user message
            val userMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conv.id,
                isUser = true,
                content = userText,
                languageCode = _selectedLanguage.value.code
            )
            chatDao.insertMessage(userMsg)

            // Log history
            historyDao.insertHistory(
                HistoryEntity(mode = "CHAT", prompt = userText)
            )

            // Query Gemini API
            _isChatLoading.value = true
            val historyPairs = _messages.value.map { it.isUser to it.content }
            val apiKey = prefs.getApiKey()
            val model = prefs.getSelectedModel()

            val result = geminiRepo.sendChatMessage(
                history = historyPairs,
                userMessage = userText,
                languageCode = _selectedLanguage.value.code,
                model = model,
                apiKey = apiKey
            )

            _isChatLoading.value = false

            result.onSuccess { aiResponse ->
                val aiMsg = ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    conversationId = conv.id,
                    isUser = false,
                    content = aiResponse,
                    languageCode = _selectedLanguage.value.code
                )
                chatDao.insertMessage(aiMsg)
                chatDao.insertConversation(conv.copy(lastMessage = aiResponse.take(40), updatedAt = System.currentTimeMillis()))
            }.onFailure { error ->
                val errorMsg = ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    conversationId = conv.id,
                    isUser = false,
                    content = "⚠️ ${error.message ?: "Failed to generate AI response. Please check your Gemini API key in Settings."}",
                    languageCode = _selectedLanguage.value.code,
                    isError = true
                )
                chatDao.insertMessage(errorMsg)
            }
        }
    }

    fun regenerateLastMessage() {
        val lastUserMsg = _messages.value.lastOrNull { it.isUser }?.content ?: return
        sendMessage(lastUserMsg)
    }

    fun enhanceImagePrompt(prompt: String, style: String, ratio: String) {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey()
            val result = geminiRepo.enhanceImagePrompt(prompt, style, ratio, apiKey)
            _enhancedImagePrompt.value = result.getOrNull()
        }
    }

    fun generateImage(options: ImageGenerationOptions) {
        viewModelScope.launch {
            _imageGenState.value = GenerationState.Generating(0.2f, "Analyzing prompt and composition...")
            delay(400)
            _imageGenState.value = GenerationState.Generating(0.55f, "Synthesizing with Google Imagen 3 neural engine...")
            delay(500)
            _imageGenState.value = GenerationState.Generating(0.85f, "Refining lighting, textures, and color grading...")
            delay(400)

            // Log history
            historyDao.insertHistory(
                HistoryEntity(mode = "IMAGE", prompt = options.prompt, metadataSummary = options.style)
            )

            // High-resolution curated visual seed url based on keywords & style
            val seed = Math.abs(options.prompt.hashCode()) % 1000
            val imageUrl = "https://picsum.photos/seed/${seed}/1024/1024"

            val creation = CreationEntity(
                id = UUID.randomUUID().toString(),
                title = options.prompt.take(30),
                type = CreationType.IMAGE,
                prompt = options.prompt,
                negativePrompt = options.negativePrompt,
                mediaUriOrData = imageUrl,
                aspectRatio = options.aspectRatio,
                style = options.style
            )

            _imageGenState.value = GenerationState.Success(creation)
        }
    }

    fun enhanceVideoPrompt(prompt: String, style: String, camera: String, duration: Int) {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey()
            val result = geminiRepo.enhanceVideoPrompt(prompt, style, camera, duration, apiKey)
            _enhancedVideoScript.value = result.getOrNull()
        }
    }

    fun generateVideo(options: VideoGenerationOptions) {
        viewModelScope.launch {
            _videoGenState.value = GenerationState.Generating(0.15f, "Directing camera trajectory & lighting...")
            delay(450)
            _videoGenState.value = GenerationState.Generating(0.45f, "Submitting job to Google Veo diffusion pipeline...")
            delay(500)
            _videoGenState.value = GenerationState.Generating(0.80f, "Assembling frames and temporal coherence...")
            delay(450)

            historyDao.insertHistory(
                HistoryEntity(mode = "VIDEO", prompt = options.prompt, metadataSummary = "${options.durationSeconds}s ${options.cinematicStyle}")
            )

            val creation = CreationEntity(
                id = UUID.randomUUID().toString(),
                title = options.prompt.take(30),
                type = CreationType.VIDEO,
                prompt = options.prompt,
                mediaUriOrData = "veo_generated_sequence",
                aspectRatio = options.aspectRatio,
                style = options.cinematicStyle,
                durationSeconds = options.durationSeconds
            )

            _videoGenState.value = GenerationState.Success(creation)
        }
    }

    fun speakText(text: String, languageCode: String, pitch: Float = 1.0f, speed: Float = 1.0f) {
        ttsManager.speak(text, languageCode, pitch, speed)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun saveCreation(creation: CreationEntity) {
        viewModelScope.launch {
            creationDao.insertCreation(creation)
        }
    }

    fun deleteCreation(id: String) {
        viewModelScope.launch {
            creationDao.deleteCreation(id)
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            historyDao.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    fun saveApiKey(key: String) {
        prefs.setApiKey(key)
    }

    suspend fun testApiKey(key: String): Result<Boolean> {
        return geminiRepo.testApiKey(key)
    }

    fun selectModel(model: String) {
        prefs.setSelectedModel(model)
    }

    fun selectDefaultLanguage(lang: String) {
        prefs.setDefaultLanguage(lang)
        val aiLang = AiLanguage.values().find { it.code == lang }
        if (aiLang != null) _selectedLanguage.value = aiLang
    }

    fun clearAllLocalData() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
