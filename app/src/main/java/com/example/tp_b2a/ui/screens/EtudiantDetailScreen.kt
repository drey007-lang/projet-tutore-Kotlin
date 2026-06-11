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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tp_b2a.data.Etudiant
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.PresenceViewModel

// ─── Fiche individuelle d'un étudiant ────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtudiantDetailScreen(
    etudiantId: Int,
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit
) {
    BackHandler { onRetour() }

    val state     by presenceViewModel.uiState.collectAsStateWithLifecycle()
    val etudiant   = state.etudiants.find { it.id == etudiantId } ?: return
    val historique = state.seancesHistorique
    val taux       = presenceViewModel.tauxPresenceEtudiant(etudiantId)
    val tauxPct    = (taux * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fiche étudiant", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = BleuPrimaire,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Carte profil ─────────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = BleuPrimaire),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${etudiant.prenom.first()}${etudiant.nom.first()}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("${etudiant.prenom} ${etudiant.nom}",
                                fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                            Text("ID : ${etudiant.id}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        }
                    }
                }
            }

            // ── Taux de présence ─────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Taux de présence global", fontWeight = FontWeight.SemiBold)
                            Text(
                                "$tauxPct%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = when {
                                    tauxPct >= 75 -> VertPresent
                                    tauxPct >= 50 -> OrangeRetard
                                    else          -> RougeAbsent
                                }
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress   = { taux },
                            modifier   = Modifier.fillMaxWidth().height(10.dp),
                            color      = when {
                                tauxPct >= 75 -> VertPresent
                                tauxPct >= 50 -> OrangeRetard
                                else          -> RougeAbsent
                            },
                            trackColor = Color(0xFFEEEEEE)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${historique.count { it.presences[etudiantId] == true }} séances présent(e) / ${historique.size} total",
                            fontSize = 13.sp,
                            color    = Color.Gray
                        )
                    }
                }
            }

            // ── Titre historique ──────────────────────────────────
            item {
                Text(
                    "Historique des séances",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = MaterialTheme.colorScheme.onBackground
                )
            }

            if (historique.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucune séance enregistrée", color = Color.Gray)
                    }
                }
            } else {
                // ── Séances ───────────────────────────────────────
                items(historique.reversed()) { record ->
                    val estPresent = record.presences[etudiantId] ?: false
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(10.dp),
                        colors    = CardDefaults.cardColors(
                            containerColor = if (estPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(record.seance.matiereNom, fontWeight = FontWeight.SemiBold)
                                Text(record.seance.date.toString(), fontSize = 12.sp, color = Color.Gray)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (estPresent) VertPresent else RougeAbsent
                            ) {
                                Text(
                                    if (estPresent) "✓ Présent" else "✗ Absent",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
