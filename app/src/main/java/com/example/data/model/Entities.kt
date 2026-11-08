package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val lastMessage: String = "",
    val languageCode: String = "en",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val isUser: Boolean,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val languageCode: String = "en",
    val isError: Boolean = false
)

enum class CreationType {
    IMAGE,
    VIDEO,
    AUDIO,
    CHAT
}

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: CreationType,
    val prompt: String,
    val negativePrompt: String = "",
    val mediaUriOrData: String,
    val aspectRatio: String = "1:1",
    val style: String = "",
    val durationSeconds: Int = 0,
    val voice: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history_entries")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String,
    val prompt: String,
    val metadataSummary: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
