package com.example.tp_b2a.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "presences",
    primaryKeys = ["etudiantId", "seanceId"],
    foreignKeys = [
        ForeignKey(
            entity = EtudiantEntity::class,
            parentColumns = ["id"],
            childColumns = ["etudiantId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SeanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["seanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PresenceEntity(
    val etudiantId: Int,
    val seanceId: Long,
    val estPresent: Boolean
)
