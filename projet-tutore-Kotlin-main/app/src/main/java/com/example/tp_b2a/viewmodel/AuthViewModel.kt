package com.example.tp_b2a.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.model.Delegue
import com.example.tp_b2a.data.model.Enseignant

// ─── ViewModel d'authentification (session en mémoire) ────────────────────

class AuthViewModel : ViewModel() {

    var enseignantConnecte by mutableStateOf<Enseignant?>(null)
        private set

    var delegueConnecte by mutableStateOf<Delegue?>(null)
        private set

    // ── Connexion enseignant ───────────────────────────────────────────────

    /** Retourne true si la connexion a réussi, false si code incorrect */
    fun connecterEnseignant(code: String): Boolean {
        enseignantConnecte = DataSource.enseignants
            .find { it.code == code.trim().uppercase() }
        return enseignantConnecte != null
    }

    // ── Connexion délégué ──────────────────────────────────────────────────

    /** Retourne true si la connexion a réussi, false si code incorrect */
    fun connecterDelegue(code: String): Boolean {
        delegueConnecte = DataSource.delegues
            .find { it.code == code.trim().uppercase() }
        return delegueConnecte != null
    }

    // ── Déconnexion ────────────────────────────────────────────────────────

    fun deconnecterEnseignant() {
        enseignantConnecte = null
    }

    fun deconnecterDelegue() {
        delegueConnecte = null
    }
}
