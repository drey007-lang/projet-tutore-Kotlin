package com.example.tp_b2a.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Delegue
import com.example.tp_b2a.data.Enseignant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ─── État d'authentification ──────────────────────────────────────────────────

data class AuthUiState(
    val delegueConnecte:     Delegue?    = null,
    val enseignantConnecte:  Enseignant? = null,
    val erreurConnexion:     Boolean     = false
)

// ─── ViewModel d'authentification ─────────────────────────────────────────────

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Connexion délégué ────────────────────────────────────────────────────
    fun connecterDelegue(code: String): Boolean {
        val delegue = DataSource.delegues.find { it.code == code.trim().uppercase() }
        _uiState.update { it.copy(delegueConnecte = delegue, erreurConnexion = delegue == null) }
        return delegue != null
    }

    // ── Connexion enseignant ─────────────────────────────────────────────────
    fun connecterEnseignant(code: String): Boolean {
        val enseignant = DataSource.enseignants.find { it.code == code.trim().uppercase() }
        _uiState.update { it.copy(enseignantConnecte = enseignant, erreurConnexion = enseignant == null) }
        return enseignant != null
    }

    // ── Déconnexion complète ─────────────────────────────────────────────────
    fun deconnecter() {
        _uiState.update { AuthUiState() }
    }

    // ── Réinitialiser l'erreur ───────────────────────────────────────────────
    fun clearErreur() {
        _uiState.update { it.copy(erreurConnexion = false) }
    }
}
