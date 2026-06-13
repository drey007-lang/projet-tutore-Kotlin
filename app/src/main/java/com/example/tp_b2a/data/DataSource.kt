package com.example.tp_b2a.data


// ─── Modèles ───────────────────────────────────────────────────────────────

data class Etudiant(
    val id: Int,
    val nom: String,
    val prenom: String,
    var estPresent: Boolean = false,
    var estEnRetard: Boolean = false,
    var justificatif: String? = null // URL ou chemin vers le justificatif
)

data class Enseignant(
    val id: Int,
    val nom: String,
    val prenom: String,
    val matiere: String,
    val code: String // code de connexion de l'enseignant
)

data class Delegue(
    val id: Int,
    val nom: String,
    val prenom: String,
    val code: String // code de connexion du délégué
)

// ─── Données fixes (codées en dur) ─────────────────────────────────────────

object DataSource {

    val etudiants = mutableListOf(
        Etudiant(1,  "Kofi",    "Ama"),
        Etudiant(2,  "Mensah",  "Kwame"),
        Etudiant(3,  "Adjei",   "Abena"),
        Etudiant(4,  "Asante",  "Kojo"),
        Etudiant(5,  "Boateng", "Akua"),
        Etudiant(6,  "Owusu",   "Kofi"),
        Etudiant(7,  "Darko",   "Esi"),
        Etudiant(8,  "Agyei",   "Kweku"),
        Etudiant(9,  "Frimpong","Adwoa"),
        Etudiant(10, "Antwi",   "Yaw"),
        Etudiant(11, "Bonsu",   "Akosua"),
        Etudiant(12, "Amoah",   "Fiifi"),
        Etudiant(13, "Ofori",   "Maame"),
        Etudiant(14, "Asiedu",  "Nana"),
        Etudiant(15, "Boateng", "Ato")
    )

    val enseignants = listOf(
        Enseignant(1, "BODJONA",  "Bataka",  "Développement Mobile", code = "PROF01"),
        Enseignant(2, "KOFFI",    "Ama",     "Base de Données",      code = "PROF02"),
        Enseignant(3, "MENSAH",   "Kwame",   "Réseaux",              code = "PROF03"),
        Enseignant(4, "ADJEI",    "Abena",   "Algorithmique",        code = "PROF04")
    )

    val delegues = listOf(
        Delegue(1, "ASANTE", "Kojo",  code = "DEL01"),
        Delegue(2, "OWUSU",  "Akua", code = "DEL02")
    )

    // Remet toutes les présences à zéro
    fun resetPresences() {
        etudiants.forEach { it.estPresent = false }
    }
}
