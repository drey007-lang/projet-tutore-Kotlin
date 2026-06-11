package com.example.tp_b2a.data.local.dao

import androidx.room.*
import com.example.tp_b2a.data.local.entities.PresenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresenceDao {
    @Query("SELECT * FROM presences WHERE seanceId = :seanceId")
    suspend fun getBySeance(seanceId: Long): List<PresenceEntity>

    @Query("""
        SELECT COUNT(*) FROM presences
        WHERE etudiantId = :etudiantId AND estPresent = 1
    """)
    suspend fun nbPresencesEtudiant(etudiantId: Int): Int

    @Query("SELECT COUNT(*) FROM seances")
    suspend fun nbTotalSeances(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presences: List<PresenceEntity>)

    @Query("SELECT * FROM presences")
    fun getAll(): Flow<List<PresenceEntity>>
}
