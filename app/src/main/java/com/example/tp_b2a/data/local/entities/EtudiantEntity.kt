package com.example.tp_b2a.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "etudiants")
data class EtudiantEntity(
    @PrimaryKey val id: Int,
    val nom: String,
    val prenom: String
)
