package com.example.tp_b2a.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tp_b2a.ui.screens.AccueilScreen
import com.example.tp_b2a.ui.screens.DelegueScreen
import com.example.tp_b2a.ui.screens.EnseignantScreen
import com.example.tp_b2a.ui.screens.EtudiantDetailScreen
import com.example.tp_b2a.ui.screens.EtudiantScreen
import com.example.tp_b2a.ui.screens.HistoriqueScreen
import com.example.tp_b2a.viewmodel.AuthViewModel
import com.example.tp_b2a.viewmodel.PresenceViewModel

// ─── Routes de navigation ──────────────────────────────────────────────────
object Route {
    const val ACCUEIL          = "accueil"
    const val ETUDIANT         = "etudiant"
    const val DELEGUE          = "delegue"
    const val ENSEIGNANT       = "enseignant"
    const val HISTORIQUE       = "historique"
    const val ETUDIANT_DETAIL  = "etudiant_detail/{etudiantId}"

    fun etudiantDetail(etudiantId: Int) = "etudiant_detail/$etudiantId"
}

// ─── Graphe de navigation principal ───────────────────────────────────────

@Composable
fun AppNavigation() {
    val navController      = rememberNavController()
    val presenceViewModel: PresenceViewModel = viewModel()
    val authViewModel: AuthViewModel         = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Route.ACCUEIL
    ) {
        // ── Accueil ────────────────────────────────────────────────────
        composable(Route.ACCUEIL) {
            AccueilScreen(
                onEtudiantClick   = { navController.navigate(Route.ETUDIANT) },
                onDelegueClick    = { navController.navigate(Route.DELEGUE) },
                onEnseignantClick = { navController.navigate(Route.ENSEIGNANT) }
            )
        }

        // ── Étudiant ───────────────────────────────────────────────────
        composable(Route.ETUDIANT) {
            EtudiantScreen(
                presenceViewModel = presenceViewModel,
                onRetour          = { navController.popBackStack() }
            )
        }

        // ── Délégué ────────────────────────────────────────────────────
        composable(Route.DELEGUE) {
            DelegueScreen(
                presenceViewModel = presenceViewModel,
                authViewModel     = authViewModel,
                onRetour          = {
                    authViewModel.deconnecterDelegue()
                    navController.popBackStack()
                }
            )
        }

        // ── Enseignant ─────────────────────────────────────────────────
        composable(Route.ENSEIGNANT) {
            EnseignantScreen(
                presenceViewModel = presenceViewModel,
                authViewModel     = authViewModel,
                onRetour          = {
                    authViewModel.deconnecterEnseignant()
                    navController.popBackStack()
                },
                onHistoriqueClick = { navController.navigate(Route.HISTORIQUE) }
            )
        }

        // ── Historique ─────────────────────────────────────────────────
        composable(Route.HISTORIQUE) {
            HistoriqueScreen(
                presenceViewModel = presenceViewModel,
                onRetour          = { navController.popBackStack() },
                onEtudiantClick   = { id ->
                    navController.navigate(Route.etudiantDetail(id))
                }
            )
        }

        // ── Détail étudiant ────────────────────────────────────────────
        composable(
            route     = Route.ETUDIANT_DETAIL,
            arguments = listOf(navArgument("etudiantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val etudiantId = backStackEntry.arguments?.getInt("etudiantId") ?: return@composable
            EtudiantDetailScreen(
                etudiantId        = etudiantId,
                presenceViewModel = presenceViewModel,
                onRetour          = { navController.popBackStack() }
            )
        }
    }
}
