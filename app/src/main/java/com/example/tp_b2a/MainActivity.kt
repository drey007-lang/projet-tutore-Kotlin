package com.example.tp_b2a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tp_b2a.ui.screens.*
import com.example.tp_b2a.ui.theme.TP_B2ATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TP_B2ATheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "loading") {
                    composable("loading") {
                        LoadingScreen(onFinished = {
                            navController.navigate("accueil") {
                                popUpTo("loading") { inclusive = true }
                            }
                        })
                    }
                    composable("accueil") {
                        AccueilScreen(
                            onEtudiantClick = { navController.navigate("etudiant") },
                            onDelegueClick = { navController.navigate("delegue") },
                            onEnseignantClick = { navController.navigate("enseignant") }
                        )
                    }
                    composable("etudiant") {
                        EtudiantScreen(
                            onRetour = { navController.popBackStack() },
                            onScanClick = { navController.navigate("scanner") }
                        )
                    }
                    composable("scanner") {
                        ScannerScreen(
                            onScanResult = { result ->
                                // Handle scan result (e.g. mark present)
                                navController.popBackStack()
                            },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                    composable("delegue") {
                        DelegueScreen(onRetour = { navController.popBackStack() })
                    }
                    composable("enseignant") {
                        EnseignantScreen(onRetour = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
