package com.example.tp_b2a.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seances")
data class SeanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // ISO format : "2025-06-11"
    val matiereNom: String,
    val enseignantNom: String
)
