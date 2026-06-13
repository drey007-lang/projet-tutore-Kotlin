package com.example.tp_b2a.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Etudiants
    @Query("SELECT * FROM etudiants ORDER BY nom ASC")
    fun getAllEtudiants(): Flow<List<EtudiantEntity>>

    @Query("SELECT COUNT(*) FROM etudiants")
    suspend fun getEtudiantCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEtudiants(etudiants: List<EtudiantEntity>)

    // Enseignants
    @Query("SELECT * FROM enseignants")
    fun getAllEnseignants(): Flow<List<EnseignantEntity>>

    @Query("SELECT * FROM enseignants WHERE code = :code LIMIT 1")
    suspend fun getEnseignantByCode(code: String): EnseignantEntity?

    @Query("SELECT * FROM enseignants WHERE (nom = :nom OR prenom = :nom) AND code = :code LIMIT 1")
    suspend fun getEnseignantByNameAndCode(nom: String, code: String): EnseignantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnseignants(enseignants: List<EnseignantEntity>)

    // Delegues
    @Query("SELECT * FROM delegues WHERE (nom = :nom OR prenom = :nom) AND code = :code LIMIT 1")
    suspend fun getDelegueByNameAndCode(nom: String, code: String): DelegueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelegues(delegues: List<DelegueEntity>)

    // Sessions & Attendance
    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecordEntity>)

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    suspend fun getAttendanceForSession(sessionId: Int): List<AttendanceRecordEntity>
}
