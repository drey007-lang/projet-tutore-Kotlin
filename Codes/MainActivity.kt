package com.example.tp_b2a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.tp_b2a.ui.screens.*
import com.example.tp_b2a.ui.theme.TP_B2ATheme

// ─── Écrans de l'application ────────────────────────────────────────────────
enum class Ecran { ACCUEIL, ETUDIANT, DELEGUE, ENSEIGNANT }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TP_B2ATheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var ecranActuel by remember { mutableStateOf(Ecran.ACCUEIL) }

    when (ecranActuel) {
        Ecran.ACCUEIL -> AccueilScreen(
            onEtudiantClick   = { ecranActuel = Ecran.ETUDIANT   },
            onDelegueClick    = { ecranActuel = Ecran.DELEGUE    },
            onEnseignantClick = { ecranActuel = Ecran.ENSEIGNANT }
        )
        Ecran.ETUDIANT -> EtudiantScreen(
            onRetour = { ecranActuel = Ecran.ACCUEIL }
        )
        Ecran.DELEGUE -> DelegueScreen(
            onRetour = { ecranActuel = Ecran.ACCUEIL }
        )
        Ecran.ENSEIGNANT -> EnseignantScreen(
            onRetour = { ecranActuel = Ecran.ACCUEIL }
        )
    }
}
