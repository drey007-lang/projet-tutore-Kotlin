# 📋 PresenZ 🚀

**PresenZ** est une application mobile Android moderne, ergonomique et performante conçue pour simplifier la gestion, la validation et le suivi des présences étudiantes. Développée dans le cadre d'un projet tuteuré au sein du **Collège de Paris Supérieur**.

---

## 👥 Membres du Groupe
*   **Goka Ayao Thomas**
*   **BAMNANTE Papiene**
*   **LARE Georges**
*   **DIALLO Rahmatoulaye**
*   **TCHAKEBERA Gabriella**

---

## 🛠️ Stack Technique
L'application repose sur les technologies les plus modernes recommandées pour le développement Android natif :
*   **Langage :** [Kotlin](https://kotlinlang.org/) (v2.2.10)
*   **Interface Utilisateur :** [Jetpack Compose](https://developer.android.com/compose) (UI déclarative moderne, Thème Rose/Pink)
*   **Persistance locale :** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite ORM)
*   **Architecture :** MVVM (Model-View-ViewModel) + Coroutines Kotlin (Asynchronisme) & StateFlow
*   **Scan de QR Codes :** [ZXing Android Embedded](https://github.com/journeyapps/zxing-android-embedded) (v4.3.0)
*   **Génération de rapports :** [iText PDF](https://itextpdf.com/) (v7.2.5) pour l'export des fiches de présence

---

## 🏛️ Architecture & Clean Code

L'application est structurée selon l'architecture **MVVM (Model-View-ViewModel)** garantissant une séparation claire des responsabilités :
*   **`data/`** : Contient le modèle de données en mémoire ([DataSource](file:///e:/PresenZ/projet-tutore-Kotlin/app/src/main/java/com/example/tp_b2a/data/DataSource.kt)), les entités Room et l'abstraction de base de données.
*   **`ui/`** : Divisé en composants réutilisables ([screens/](file:///e:/PresenZ/projet-tutore-Kotlin/app/src/main/java/com/example/tp_b2a/ui/screens) et [theme/](file:///e:/PresenZ/projet-tutore-Kotlin/app/src/main/java/com/example/tp_b2a/ui/theme)).
*   **ViewModel** : Géré par [MainViewModel](file:///e:/PresenZ/projet-tutore-Kotlin/app/src/main/java/com/example/tp_b2a/ui/MainViewModel.kt) qui centralise l'accès à la base de données Room et expose des états réactifs via `StateFlow`.

---

## 💾 Schéma de Base de Données (Room)

La base de données locale SQL complète est modélisée dans [Entities.kt](file:///e:/PresenZ/projet-tutore-Kotlin/app/src/main/java/com/example/tp_b2a/data/local/Entities.kt) :
```
┌─────────────────┐       ┌─────────────────┐       ┌────────────────────────┐
│ EtudiantEntity  │       │ EnseignantEntity│       │ AttendanceRecordEntity │
├─────────────────┤       ├─────────────────┤       ├────────────────────────┤
│ id (PK)         │       │ id (PK)         │       │ id (PK)                │
│ nom             │       │ nom             │       │ sessionId (FK)         │
│ prenom          │       │ prenom          │       │ studentId (FK)         │
└─────────────────┘       │ matiere         │       │ isPresent (Boolean)    │
                          │ code            │       └────────────────────────┘
┌─────────────────┐       └─────────────────┘
│  DelegueEntity  │
├─────────────────┤       ┌─────────────────┐
│ id (PK)         │       │  SessionEntity  │
│ nom             │       ├─────────────────┤
│ prenom          │       │ id (PK)         │
│ code            │       │ teacherId (FK)  │
└─────────────────┘       │ date (Long)     │
                          │ matiere (String)│
                          │ isValidated     │
                          └─────────────────┘
```

---

## 🌟 Fonctionnalités Principales

### 1. Profil Étudiant (🎓)
*   Consulter la liste de classe.
*   Rechercher son nom rapidement via une barre de recherche interactive.
*   **Scanner QR Code** : L'étudiant peut scanner le QR Code de la séance pour enregistrer instantanément sa présence en base de données locale.
*   Déclarer un retard ou justifier une absence en joignant virtuellement un justificatif.

### 2. Profil Délégué (📌)
*   Authentification sécurisée avec code unique (ex: `DEL01`).
*   Tableau de bord statistique interactif (Nombre de présents, absents, retards).
*   Vérifier, corriger et valider la feuille de présence de la classe avant transmission à l'enseignant.
*   Sauvegarde instantanée des modifications dans Room.

### 3. Profil Enseignant (👨‍🏫)
*   Authentification sécurisée par code de cours unique (ex: `PROF01`).
*   Suivi visuel en temps réel avec barre de progression de présence globale.
*   **Tirage au sort (Appel Aléatoire) 🎲** : Outil interactif choisissant de manière équitable un étudiant présent pour répondre aux questions.
*   **Export de Rapport PDF 📄** : Génère une fiche de présence officielle et stylisée avec code couleur (Vert = Présent, Rouge = Absent), prête à être partagée ou imprimée.
*   Validation et signature numérique de la séance.

---

## 🔑 Identifiants de Test (Credentials)

Pour tester rapidement les différents profils de l'application, voici les comptes de test pré-configurés en base de données :

### 👨‍🏫 Profil Enseignant
*   **Enseignant 1 (Matière : Développement Mobile) :**
    *   **Identifiant (Nom) :** `BODJONA` (ou prénom : `Bataka`)
    *   **Code de connexion / Mot de passe :** `PROF01`
*   **Enseignant 2 (Matière : Base de Données) :**
    *   **Identifiant (Nom) :** `KOFFI` (ou prénom : `Ama`)
    *   **Code de connexion / Mot de passe :** `PROF02`
*   **Enseignant 3 (Matière : Réseaux) :**
    *   **Identifiant (Nom) :** `MENSAH` (ou prénom : `Kwame`)
    *   **Code de connexion / Mot de passe :** `PROF03`

### 📌 Profil Délégué
*   **Délégué 1 :**
    *   **Identifiant (Nom) :** `ASANTE` (ou prénom : `Kojo`)
    *   **Code de connexion / Mot de passe :** `DEL01`
*   **Délégué 2 :**
    *   **Identifiant (Nom) :** `OWUSU` (ou prénom : `Akua`)
    *   **Code de connexion / Mot de passe :** `DEL02`

### 🎓 Profil Étudiant (Scan QR Code)
*   L'étudiant choisit son nom dans la liste de classe.
*   **Pour le test de scan QR Code :** Générez un code QR contenant simplement le chiffre correspondant à l'ID de l'étudiant (ex: QR code avec le texte `"1"` pour *Kofi Ama*, `"2"` pour *Mensah Kwame*, etc.) et scannez-le depuis l'application.

---

## 🚀 Installation & Exécution

### Prérequis
*   Android Studio Ladybug (2024.2.1) ou supérieur.
*   SDK Android API 36 installé.
*   Kotlin 2.2.10.

### Démarrage rapide
1.  Clonez ce dépôt :
    ```bash
    git clone https://github.com/drey007-lang/projet-tutore-Kotlin.git
    ```
2.  Ouvrez le projet dans **Android Studio**.
3.  Laissez Gradle synchroniser le projet.
4.  Démarrez un émulateur ou branchez un appareil physique Android.
5.  Cliquez sur le bouton **Run** (`app`).

---

*Dernière mise à jour effectuée.*

