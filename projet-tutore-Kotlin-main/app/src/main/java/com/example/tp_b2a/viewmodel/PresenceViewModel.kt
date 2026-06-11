package com.example.tp_b2a.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.model.Enseignant
import com.example.tp_b2a.data.model.Etudiant
import com.example.tp_b2a.data.model.Seance
import com.example.tp_b2a.data.repository.PresenceRepository
import java.util.UUID

// ─── ViewModel principal — État des présences + Historique ────────────────

class PresenceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PresenceRepository(application)

    // ── Liste statique des étudiants ───────────────────────────────────────
    val etudiants: List<Etudiant> = DataSource.etudiants

    // ── Présences en cours (etudiantId -> estPresent) ──────────────────────
    private val _presences = mutableStateMapOf<Int, Boolean>()
    val presences: Map<Int, Boolean> get() = _presences

    // ── Historique des séances validées ────────────────────────────────────
    private val _seances = mutableStateOf<List<Seance>>(repository.getSeances())
    val seances: List<Seance> get() = _seances.value

    // ── Actions sur les présences ──────────────────────────────────────────

    /** Marque un étudiant comme présent (ne peut que s'activer, pas se toggle) */
    fun marquerPresent(etudiantId: Int) {
        _presences[etudiantId] = true
    }

    /** Bascule la présence d'un étudiant (pour le délégué) */
    fun togglePresence(etudiantId: Int) {
        _presences[etudiantId] = !(_presences[etudiantId] ?: false)
    }

    fun estPresent(etudiantId: Int): Boolean = _presences[etudiantId] ?: false

    fun nbPresents(): Int = _presences.count { it.value }
    fun nbAbsents(): Int = etudiants.size - nbPresents()
    fun taux(): Int = if (etudiants.isNotEmpty()) (nbPresents() * 100) / etudiants.size else 0

    // ── Validation et sauvegarde d'une séance ─────────────────────────────

    /** Valide et persiste la séance. Appelé par l'Enseignant. */
    fun confirmerSeance(enseignant: Enseignant) {
        // S'assurer que tous les étudiants ont une entrée (absent par défaut)
        val presencesCompletes = etudiants.associate { e ->
            e.id.toString() to (_presences[e.id] ?: false)
        }
        val seance = Seance(
            id            = UUID.randomUUID().toString(),
            date          = System.currentTimeMillis(),
            enseignantId  = enseignant.id,
            matiereNom    = enseignant.matiere,
            enseignantNom = enseignant.nomComplet,
            presences     = presencesCompletes
        )
        repository.sauvegarderSeance(seance)
        _seances.value = repository.getSeances()
        resetPresences()
    }

    /** Remet toutes les présences à zéro (nouvelle séance) */
    fun resetPresences() {
        _presences.clear()
    }

    // ── Statistiques par étudiant ──────────────────────────────────────────

    /** Taux de présence global d'un étudiant sur toutes les séances (%) */
    fun tauxPresenceEtudiant(etudiantId: Int): Int {
        if (_seances.value.isEmpty()) return 0
        val nbPresent = _seances.value.count { it.estPresent(etudiantId) }
        return (nbPresent * 100) / _seances.value.size
    }

    /** Liste des séances avec le statut de présence d'un étudiant */
    fun seancesEtudiant(etudiantId: Int): List<Pair<Seance, Boolean>> {
        return _seances.value.map { seance ->
            seance to seance.estPresent(etudiantId)
        }
    }

    /** Supprime tout l'historique (admin) */
    fun supprimerHistorique() {
        repository.supprimerTout()
        _seances.value = emptyList()
        resetPresences()
    }
}
