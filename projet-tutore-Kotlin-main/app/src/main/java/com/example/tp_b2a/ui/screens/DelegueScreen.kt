package com.example.tp_b2a.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.model.Etudiant
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.AuthViewModel
import com.example.tp_b2a.viewmodel.PresenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueScreen(
    presenceViewModel: PresenceViewModel,
    authViewModel: AuthViewModel,
    onRetour: () -> Unit
) {
    var valide by remember { mutableStateOf(false) }

    BackHandler { onRetour() }

    when {
        // ── 1. Connexion ──────────────────────────────────────────────
        authViewModel.delegueConnecte == null -> {
            LoginScreen(
                titre      = "Délégué",
                emoji      = "📌",
                couleur    = CouleurDelegue,
                onRetour   = onRetour,
                onConnexion = { code -> !authViewModel.connecterDelegue(code) }
            )
        }

        // ── 2. Tableau de bord ────────────────────────────────────────
        !valide -> {
            DelegueTableauBord(
                delegueNom        = authViewModel.delegueConnecte!!.nomComplet,
                presenceViewModel = presenceViewModel,
                onRetour          = onRetour,
                onValider         = { valide = true }
            )
        }

        // ── 3. Confirmation ───────────────────────────────────────────
        else -> {
            ConfirmationScreen(
                message  = "Liste validée !",
                detail   = "La feuille de présence a été transmise à l'enseignant.",
                couleur  = CouleurDelegue,
                onRetour = onRetour
            )
        }
    }
}

// ─── Tableau de bord délégué ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DelegueTableauBord(
    delegueNom: String,
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit,
    onValider: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    // Lecture réactive depuis le ViewModel
    val presences   = presenceViewModel.presences
    val etudiants   = presenceViewModel.etudiants
    val nbPresents  = presenceViewModel.nbPresents()
    val nbAbsents   = presenceViewModel.nbAbsents()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tableau de bord", fontWeight = FontWeight.Bold)
                        Text("Délégué : $delegueNom", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh,
                            contentDescription = "Réinitialiser", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = CouleurDelegue,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick  = onValider,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CouleurDelegue)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Valider la liste ($nbPresents présents)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Statistiques
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(Modifier.weight(1f), "Présents", "$nbPresents", VertPresent)
                StatCard(Modifier.weight(1f), "Absents",  "$nbAbsents",  RougeAbsent)
                StatCard(Modifier.weight(1f), "Total",    "${etudiants.size}", BleuClair)
            }

            Text(
                "Liste de présence — le délégué peut modifier",
                fontWeight = FontWeight.Medium,
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(etudiants, key = { it.id }) { etudiant ->
                    val present = presences[etudiant.id] ?: false
                    DelegueEtudiantItem(
                        etudiant = etudiant,
                        present  = present,
                        onToggle = { presenceViewModel.togglePresence(etudiant.id) }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }

    // Dialog de confirmation reset
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title   = { Text("Réinitialiser ?") },
            text    = { Text("Toutes les présences cochées seront effacées.") },
            confirmButton = {
                TextButton(onClick = {
                    presenceViewModel.resetPresences()
                    showResetDialog = false
                }) { Text("Réinitialiser", color = RougeAbsent) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Annuler") }
            }
        )
    }
}

// ─── Item délégué (modifiable) ────────────────────────────────────────────

@Composable
private fun DelegueEtudiantItem(
    etudiant: Etudiant,
    present: Boolean,
    onToggle: () -> Unit
) {
    Card(
        onClick   = onToggle,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (present) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (present) VertPresent else RougeAbsent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(etudiant.initiales, color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(etudiant.nomComplet, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (present) VertPresent else RougeAbsent
            ) {
                Text(
                    if (present) "Présent" else "Absent",
                    color    = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}
