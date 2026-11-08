package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CreationEntity
import com.example.data.model.CreationType
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {
    @Query("SELECT * FROM creations ORDER BY createdAt DESC")
    fun getAllCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE type = :type ORDER BY createdAt DESC")
    fun getCreationsByType(type: CreationType): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE title LIKE '%' || :query || '%' OR prompt LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchCreations(query: String): Flow<List<CreationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: CreationEntity)

    @Query("DELETE FROM creations WHERE id = :id")
    suspend fun deleteCreation(id: String)

    @Query("SELECT COUNT(*) FROM creations WHERE type = :type")
    fun getCountByType(type: CreationType): Flow<Int>

    @Query("SELECT COUNT(*) FROM creations")
    fun getTotalCount(): Flow<Int>
}
