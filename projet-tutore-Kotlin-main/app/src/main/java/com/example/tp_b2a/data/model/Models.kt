package com.example.tp_b2a.data.model

// ─── Modèles de domaine ────────────────────────────────────────────────────

data class Etudiant(
    val id: Int,
    val nom: String,
    val prenom: String
) {
    val initiales: String get() = "${prenom.firstOrNull() ?: ""}${nom.firstOrNull() ?: ""}"
    val nomComplet: String get() = "$prenom $nom"
}

data class Enseignant(
    val id: Int,
    val nom: String,
    val prenom: String,
    val matiere: String,
    val code: String
) {
    val nomComplet: String get() = "$prenom $nom"
    val initiales: String get() = "${prenom.firstOrNull() ?: ""}${nom.firstOrNull() ?: ""}"
}

data class Delegue(
    val id: Int,
    val nom: String,
    val prenom: String,
    val code: String
) {
    val nomComplet: String get() = "$prenom $nom"
}

// ─── Modèle pour une séance (persistée) ───────────────────────────────────

data class Seance(
    val id: String = "",
    val date: Long = 0L,                          // Timestamp ms
    val enseignantId: Int = 0,
    val matiereNom: String = "",
    val enseignantNom: String = "",
    val presences: Map<String, Boolean> = emptyMap() // "etudiantId" -> present
) {
    val nbPresents: Int get() = presences.count { it.value }
    val nbAbsents: Int get() = presences.count { !it.value }
    val total: Int get() = presences.size
    val taux: Int get() = if (total > 0) (nbPresents * 100) / total else 0

    fun estPresent(etudiantId: Int): Boolean = presences[etudiantId.toString()] ?: false
}
