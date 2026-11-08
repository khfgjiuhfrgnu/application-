package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.CreationEntity
import com.example.data.model.CreationType
import com.example.data.model.HistoryEntity

class Converters {
    @TypeConverter
    fun fromCreationType(type: CreationType): String = type.name

    @TypeConverter
    fun toCreationType(value: String): CreationType = try {
        CreationType.valueOf(value)
    } catch (_: Exception) {
        CreationType.IMAGE
    }
}

@Database(
    entities = [
        ConversationEntity::class,
        ChatMessageEntity::class,
        CreationEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun creationDao(): CreationDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "khaled_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
