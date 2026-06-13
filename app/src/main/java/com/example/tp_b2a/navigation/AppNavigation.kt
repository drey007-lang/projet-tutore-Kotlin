package com.example.tp_b2a.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tp_b2a.ui.screens.*
import com.example.tp_b2a.viewmodel.AuthViewModel
import com.example.tp_b2a.viewmodel.PresenceViewModel

// ─── Routes de navigation ────────────────────────────────────────────────────

object Routes {
    const val ACCUEIL          = "accueil"
    const val ETUDIANT         = "etudiant"
    const val DELEGUE          = "delegue"
    const val ENSEIGNANT       = "enseignant"
    const val HISTORIQUE       = "historique"
    const val ETUDIANT_DETAIL  = "etudiant_detail/{etudiantId}"

    fun etudiantDetail(id: Int) = "etudiant_detail/$id"
}

// ─── NavHost principal ───────────────────────────────────────────────────────

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    presenceViewModel: PresenceViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController    = navController,
        startDestination = Routes.ACCUEIL,
        enterTransition  = { fadeIn() + slideInHorizontally(initialOffsetX = { it / 3 }) },
        exitTransition   = { fadeOut() + slideOutHorizontally(targetOffsetX = { -it / 3 }) },
        popEnterTransition  = { fadeIn() + slideInHorizontally(initialOffsetX = { -it / 3 }) },
        popExitTransition   = { fadeOut() + slideOutHorizontally(targetOffsetX = { it / 3 }) }
    ) {
        composable(Routes.ACCUEIL) {
            AccueilScreen(
                onEtudiantClick   = { navController.navigate(Routes.ETUDIANT) },
                onDelegueClick    = { navController.navigate(Routes.DELEGUE) },
                onEnseignantClick = { navController.navigate(Routes.ENSEIGNANT) }
            )
        }

        composable(Routes.ETUDIANT) {
            EtudiantScreen(
                presenceViewModel = presenceViewModel,
                onRetour = { navController.popBackStack() }
            )
        }

        composable(Routes.DELEGUE) {
            DelegueScreen(
                presenceViewModel = presenceViewModel,
                authViewModel     = authViewModel,
                onRetour = { navController.popBackStack() }
            )
        }

        composable(Routes.ENSEIGNANT) {
            EnseignantScreen(
                presenceViewModel = presenceViewModel,
                authViewModel     = authViewModel,
                onRetour     = { navController.popBackStack() },
                onHistorique = { navController.navigate(Routes.HISTORIQUE) }
            )
        }

        composable(Routes.HISTORIQUE) {
            HistoriqueScreen(
                presenceViewModel = presenceViewModel,
                onRetour = { navController.popBackStack() }
            )
        }

        composable(Routes.ETUDIANT_DETAIL) { backStackEntry ->
            val etudiantId = backStackEntry.arguments?.getString("etudiantId")?.toIntOrNull() ?: return@composable
            EtudiantDetailScreen(
                etudiantId        = etudiantId,
                presenceViewModel = presenceViewModel,
                onRetour          = { navController.popBackStack() }
            )
        }
    }
}
