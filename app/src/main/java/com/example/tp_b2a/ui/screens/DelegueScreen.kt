package com.example.tp_b2a.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tp_b2a.data.Delegue
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.AuthViewModel
import com.example.tp_b2a.viewmodel.PresenceViewModel

private val CouleurDelegue = Color(0xFF6A1B9A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueScreen(
    presenceViewModel: PresenceViewModel,
    authViewModel: AuthViewModel,
    onRetour: () -> Unit
) {
    BackHandler { onRetour() }

    val authState     by authViewModel.uiState.collectAsStateWithLifecycle()
    val presenceState by presenceViewModel.uiState.collectAsStateWithLifecycle()

    var valide by remember { mutableStateOf(false) }

    when {
        authState.delegueConnecte == null -> {
            // ── Écran de connexion ────────────────────────────────
            LoginScreen(
                titre    = "Délégué",
                emoji    = "📌",
                couleur  = CouleurDelegue,
                onRetour = onRetour,
                onConnexion = { code ->
                    !authViewModel.connecterDelegue(code)
                }
            )
        }
        !valide -> {
            // ── Tableau de bord délégué ───────────────────────────
            DelegueTableauBord(
                delegue        = authState.delegueConnecte!!,
                presenceState  = presenceState,
                presenceViewModel = presenceViewModel,
                onRetour       = {
                    authViewModel.deconnecter()
                    onRetour()
                },
                onValider = { valide = true }
            )
        }
        else -> {
            // ── Confirmation ──────────────────────────────────────
            ConfirmationScreen(
                message  = "Liste validée !",
                detail   = "La fiche de présence a été transmise à l'enseignant.",
                couleur  = CouleurDelegue,
                onRetour = {
                    authViewModel.deconnecter()
                    onRetour()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueTableauBord(
    delegue: Delegue,
    presenceState: com.example.tp_b2a.viewmodel.PresenceUiState,
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit,
    onValider: () -> Unit
) {
    val etudiants  = presenceState.etudiants
    val nbPresents = presenceState.presences.values.count { it }
    val nbAbsents  = etudiants.size - nbPresents

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tableau de bord", fontWeight = FontWeight.Bold)
                        Text(
                            "Délégué : ${delegue.prenom} ${delegue.nom}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
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
                    onClick = onValider,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CouleurDelegue)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Valider la liste", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            // ── Résumé statistiques ──────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(Modifier.weight(1f), "Présents", "$nbPresents", VertPresent)
                StatCard(Modifier.weight(1f), "Absents",  "$nbAbsents",  RougeAbsent)
                StatCard(Modifier.weight(1f), "Total",    "${etudiants.size}", BleuClair)
            }

            Text(
                "Liste de présence",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(etudiants) { etudiant ->
                    val estPresent = presenceState.presences[etudiant.id] ?: false
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = if (estPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (estPresent) VertPresent else RougeAbsent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${etudiant.prenom.first()}${etudiant.nom.first()}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${etudiant.prenom} ${etudiant.nom}", fontWeight = FontWeight.Medium)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (estPresent) VertPresent else RougeAbsent
                            ) {
                                Text(
                                    text = if (estPresent) "Présent" else "Absent",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, valeur: String, couleur: Color) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = couleur),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valeur, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}
