package com.example.tp_b2a.data.repository

import com.example.tp_b2a.data.local.AppDatabase
import com.example.tp_b2a.data.local.entities.PresenceEntity
import com.example.tp_b2a.data.local.entities.SeanceEntity
import com.example.tp_b2a.viewmodel.SeanceAvecPresences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

// ─── Repository — interface entre ViewModel et Room ─────────────────────────

class PresenceRepository(private val db: AppDatabase) {

    private val etudiantDao = db.etudiantDao()
    private val seanceDao   = db.seanceDao()
    private val presenceDao = db.presenceDao()

    // ── Flux réactifs ────────────────────────────────────────────────────────

    val etudiants = etudiantDao.getAll()

    /** Flux combiné : séances + présences associées */
    val seancesAvecPresences: Flow<List<SeanceAvecPresences>> =
        seanceDao.getAll().combine(presenceDao.getAll()) { seances, presences ->
            seances.map { seanceEntity ->
                val presMap = presences
                    .filter { it.seanceId == seanceEntity.id }
                    .associate { it.etudiantId to it.estPresent }
                SeanceAvecPresences(
                    seance = com.example.tp_b2a.data.Seance(
                        id            = seanceEntity.id,
                        date          = LocalDate.parse(seanceEntity.date),
                        matiereNom    = seanceEntity.matiereNom,
                        enseignantNom = seanceEntity.enseignantNom
                    ),
                    presences = presMap
                )
            }
        }

    // ── Écriture ─────────────────────────────────────────────────────────────

    suspend fun enregistrerSeance(
        matiereNom: String,
        enseignantNom: String,
        presencesMap: Map<Int, Boolean>
    ): Long {
        val seanceId = seanceDao.insert(
            SeanceEntity(
                date          = LocalDate.now().toString(),
                matiereNom    = matiereNom,
                enseignantNom = enseignantNom
            )
        )
        val entities = presencesMap.map { (etudiantId, present) ->
            PresenceEntity(etudiantId, seanceId, present)
        }
        presenceDao.insertAll(entities)
        return seanceId
    }

    // ── Stats ────────────────────────────────────────────────────────────────

    suspend fun tauxPresence(etudiantId: Int): Float {
        val total = presenceDao.nbTotalSeances()
        if (total == 0) return 0f
        val presents = presenceDao.nbPresencesEtudiant(etudiantId)
        return presents.toFloat() / total
    }
}
