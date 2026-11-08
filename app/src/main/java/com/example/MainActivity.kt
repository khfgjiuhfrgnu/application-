package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.data.model.NavDestination
import com.example.ui.MainViewModel
import com.example.ui.components.KhaledBottomNav
import com.example.ui.components.KhaledDrawerContent
import com.example.ui.components.KhaledTopBar
import com.example.ui.screens.AudioStudioScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreationsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ImageGenScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VideoGenScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.KhaledAiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KhaledAiTheme {
                KhaledAiApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KhaledAiApp(viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentDestination by viewModel.currentDestination.collectAsState()
    val initialConsolePrompt by viewModel.initialConsolePrompt.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val currentConversation by viewModel.currentConversation.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val creations by viewModel.creations.collectAsState()
    val historyEntries by viewModel.historyEntries.collectAsState()

    val imageGenState by viewModel.imageGenState.collectAsState()
    val enhancedImagePrompt by viewModel.enhancedImagePrompt.collectAsState()
    val videoGenState by viewModel.videoGenState.collectAsState()
    val enhancedVideoScript by viewModel.enhancedVideoScript.collectAsState()

    val isTtsPlaying by viewModel.ttsManager.isPlaying.collectAsState()
    val ttsAmplitudes by viewModel.ttsManager.waveformAmplitudes.collectAsState()

    val selectedModel by viewModel.prefs.selectedModelFlow.collectAsState()
    val currentApiKey by viewModel.prefs.apiKeyFlow.collectAsState()
    val defaultLanguage by viewModel.prefs.defaultLanguageFlow.collectAsState()

    // Handle back button smoothly
    BackHandler(enabled = drawerState.isOpen || currentDestination != NavDestination.DASHBOARD) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            viewModel.navigate(NavDestination.DASHBOARD)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            KhaledDrawerContent(
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    viewModel.navigate(destination)
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                KhaledTopBar(
                    title = currentDestination.title,
                    modelName = selectedModel,
                    onMenuClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onSettingsClick = {
                        viewModel.navigate(NavDestination.SETTINGS)
                    }
                )
            },
            bottomBar = {
                KhaledBottomNav(
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        viewModel.navigate(destination)
                    }
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    NavDestination.DASHBOARD -> {
                        DashboardScreen(
                            creationsCount = creations.size,
                            chatCount = conversations.size,
                            recentCreations = creations,
                            onNavigate = { destination ->
                                viewModel.navigate(destination)
                            },
                            onLaunchWithPrompt = { destination, prompt ->
                                viewModel.launchWithPrompt(destination, prompt)
                            }
                        )
                    }

                    NavDestination.CHAT -> {
                        ChatScreen(
                            currentConversation = currentConversation,
                            conversations = conversations,
                            messages = messages,
                            isLoading = isChatLoading,
                            selectedLanguage = selectedLanguage,
                            onSelectLanguage = { lang -> viewModel.selectLanguage(lang) },
                            onSendMessage = { text ->
                                viewModel.sendMessage(text)
                                viewModel.clearInitialPrompt()
                            },
                            onNewConversation = { viewModel.startNewConversation() },
                            onSelectConversation = { conv -> viewModel.selectConversation(conv) },
                            onDeleteConversation = { id -> viewModel.deleteConversation(id) },
                            onRegenerateResponse = { viewModel.regenerateLastMessage() },
                            onSpeakText = { text, lang -> viewModel.speakText(text, lang) },
                            initialPrompt = initialConsolePrompt
                        )
                    }

                    NavDestination.IMAGE -> {
                        ImageGenScreen(
                            generationState = imageGenState,
                            onGenerateImage = { options ->
                                viewModel.generateImage(options)
                                viewModel.clearInitialPrompt()
                            },
                            onEnhancePrompt = { prompt, style, ratio ->
                                viewModel.enhanceImagePrompt(prompt, style, ratio)
                            },
                            enhancedPrompt = enhancedImagePrompt,
                            onSaveToCreations = { creation -> viewModel.saveCreation(creation) },
                            initialPrompt = initialConsolePrompt
                        )
                    }

                    NavDestination.VIDEO -> {
                        VideoGenScreen(
                            generationState = videoGenState,
                            onGenerateVideo = { options ->
                                viewModel.generateVideo(options)
                                viewModel.clearInitialPrompt()
                            },
                            onEnhancePrompt = { prompt, style, camera, duration ->
                                viewModel.enhanceVideoPrompt(prompt, style, camera, duration)
                            },
                            enhancedScript = enhancedVideoScript,
                            onSaveToCreations = { creation -> viewModel.saveCreation(creation) },
                            initialPrompt = initialConsolePrompt
                        )
                    }

                    NavDestination.AUDIO -> {
                        AudioStudioScreen(
                            isPlaying = isTtsPlaying,
                            amplitudes = ttsAmplitudes,
                            onSpeak = { text, lang, pitch, speed ->
                                viewModel.speakText(text, lang, pitch, speed)
                                viewModel.clearInitialPrompt()
                            },
                            onStop = { viewModel.stopSpeaking() },
                            onSaveAudioCreation = { creation -> viewModel.saveCreation(creation) },
                            initialText = initialConsolePrompt
                        )
                    }

                    NavDestination.CREATIONS -> {
                        CreationsScreen(
                            creations = creations,
                            onDeleteCreation = { id -> viewModel.deleteCreation(id) },
                            onPlayAudio = { prompt -> viewModel.speakText(prompt, "en") }
                        )
                    }

                    NavDestination.HISTORY -> {
                        HistoryScreen(
                            historyEntries = historyEntries,
                            onDeleteEntry = { id -> viewModel.deleteHistory(id) },
                            onClearAll = { viewModel.clearAllHistory() },
                            onReusePrompt = { destination, prompt ->
                                viewModel.launchWithPrompt(destination, prompt)
                            }
                        )
                    }

                    NavDestination.SETTINGS -> {
                        SettingsScreen(
                            currentApiKey = currentApiKey,
                            onSaveApiKey = { key -> viewModel.saveApiKey(key) },
                            onTestApiKey = { key -> viewModel.testApiKey(key) },
                            selectedModel = selectedModel,
                            onSelectModel = { model -> viewModel.selectModel(model) },
                            defaultLanguage = defaultLanguage,
                            onSelectDefaultLanguage = { lang -> viewModel.selectDefaultLanguage(lang) },
                            onClearAllData = { viewModel.clearAllLocalData() }
                        )
                    }
                }
            }
        }
    }
}
