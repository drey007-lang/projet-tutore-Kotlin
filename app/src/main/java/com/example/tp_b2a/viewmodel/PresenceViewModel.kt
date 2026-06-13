package com.example.tp_b2a.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Etudiant
import com.example.tp_b2a.data.Seance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

// ─── État des présences ──────────────────────────────────────────────────────

data class PresenceUiState(
    val etudiants: List<Etudiant> = DataSource.etudiants,
    val presences: Map<Int, Boolean> = emptyMap(),   // etudiantId → estPresent
    val seanceValidee: Boolean = false,
    val seancesHistorique: List<SeanceAvecPresences> = emptyList()
)

data class SeanceAvecPresences(
    val seance: Seance,
    val presences: Map<Int, Boolean>  // etudiantId → estPresent
)

// ─── ViewModel partagé entre tous les écrans ─────────────────────────────────

class PresenceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PresenceUiState())
    val uiState: StateFlow<PresenceUiState> = _uiState.asStateFlow()

    // ── Bascule présence d'un étudiant ───────────────────────────────────────
    fun togglePresence(etudiantId: Int) {
        _uiState.update { state ->
            val actuel = state.presences[etudiantId] ?: false
            state.copy(presences = state.presences + (etudiantId to !actuel))
        }
    }

    // ── Marquer un étudiant présent directement ──────────────────────────────
    fun marquerPresent(etudiantId: Int) {
        _uiState.update { state ->
            state.copy(presences = state.presences + (etudiantId to true))
        }
    }

    // ── Confirmer la séance et sauvegarder dans l'historique ─────────────────
    fun confirmerSeance(matiereNom: String, enseignantNom: String) {
        val state = _uiState.value
        val seance = Seance(
            id            = System.currentTimeMillis(),
            date          = LocalDate.now(),
            matiereNom    = matiereNom,
            enseignantNom = enseignantNom
        )
        val record = SeanceAvecPresences(seance = seance, presences = state.presences)
        _uiState.update { s ->
            s.copy(
                seanceValidee    = true,
                seancesHistorique = s.seancesHistorique + record
            )
        }
    }

    // ── Réinitialiser pour une nouvelle séance ───────────────────────────────
    fun nouvelleSeance() {
        _uiState.update { it.copy(presences = emptyMap(), seanceValidee = false) }
    }

    // ── Nombre de présents ───────────────────────────────────────────────────
    fun nbPresents(): Int = _uiState.value.presences.values.count { it }
    fun nbAbsents(): Int  = _uiState.value.etudiants.size - nbPresents()

    // ── Taux de présence global (historique) par étudiant ────────────────────
    fun tauxPresenceEtudiant(etudiantId: Int): Float {
        val historique = _uiState.value.seancesHistorique
        if (historique.isEmpty()) return 0f
        val presents = historique.count { it.presences[etudiantId] == true }
        return presents.toFloat() / historique.size
    }
}
