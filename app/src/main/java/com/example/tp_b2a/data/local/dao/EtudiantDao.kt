package com.example.tp_b2a.data.local.dao

import androidx.room.*
import com.example.tp_b2a.data.local.entities.EtudiantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EtudiantDao {
    @Query("SELECT * FROM etudiants ORDER BY nom")
    fun getAll(): Flow<List<EtudiantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(etudiants: List<EtudiantEntity>)

    @Delete
    suspend fun delete(etudiant: EtudiantEntity)
}
