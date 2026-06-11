package com.example.tp_b2a.data.local.dao

import androidx.room.*
import com.example.tp_b2a.data.local.entities.SeanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeanceDao {
    @Query("SELECT * FROM seances ORDER BY date DESC")
    fun getAll(): Flow<List<SeanceEntity>>

    @Insert
    suspend fun insert(seance: SeanceEntity): Long

    @Delete
    suspend fun delete(seance: SeanceEntity)
}
