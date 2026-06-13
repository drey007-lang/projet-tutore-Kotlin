package com.example.tp_b2a.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "etudiants")
data class EtudiantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val prenom: String
)

@Entity(tableName = "enseignants")
data class EnseignantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val prenom: String,
    val matiere: String,
    val code: String
)

@Entity(tableName = "delegues")
data class DelegueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val prenom: String,
    val code: String
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teacherId: Int,
    val date: Long,
    val matiere: String,
    val isValidated: Boolean = false
)

@Entity(tableName = "attendance_records", primaryKeys = ["sessionId", "studentId"])
data class AttendanceRecordEntity(
    val sessionId: Int,
    val studentId: Int,
    val isPresent: Boolean
)
