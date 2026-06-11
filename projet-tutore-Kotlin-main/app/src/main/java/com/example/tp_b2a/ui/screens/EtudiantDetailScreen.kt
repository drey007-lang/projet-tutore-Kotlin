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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.PresenceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Fiche individuelle d'un étudiant ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtudiantDetailScreen(
    etudiantId: Int,
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit
) {
    BackHandler { onRetour() }

    val etudiant = DataSource.etudiants.find { it.id == etudiantId }
        ?: run { onRetour(); return }

    val seancesEtudiant = presenceViewModel.seancesEtudiant(etudiantId)
    val taux            = presenceViewModel.tauxPresenceEtudiant(etudiantId)
    val nbPresent       = seancesEtudiant.count { it.second }
    val nbAbsent        = seancesEtudiant.count { !it.second }

    val couleur = when {
        taux >= 75 -> VertPresent
        taux >= 50 -> Color(0xFFE65100)
        else       -> RougeAbsent
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(etudiant.nomComplet, fontWeight = FontWeight.Bold)
                        Text("Fiche de présence", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = BleuPrimaire,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (seancesEtudiant.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.padding(32.dp)
                ) {
                    Text("📭", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Aucune donnée", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Les données de présence de ${etudiant.prenom} apparaîtront ici après la première séance.",
                        fontSize  = 14.sp,
                        color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Carte profil ───────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = BleuPrimaire)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(etudiant.initiales,
                                color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(etudiant.nomComplet,
                            fontSize   = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(16.dp))

                        // Taux de présence circulaire (barre linéaire)
                        Text(
                            "Taux de présence global",
                            color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "$taux %",
                            fontSize   = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = when {
                                taux >= 75 -> Color(0xFFA5D6A7)
                                taux >= 50 -> Color(0xFFFFCC80)
                                else       -> Color(0xFFEF9A9A)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress   = { taux / 100f },
                            modifier   = Modifier.fillMaxWidth(0.8f).height(8.dp),
                            color      = when {
                                taux >= 75 -> Color(0xFFA5D6A7)
                                taux >= 50 -> Color(0xFFFFCC80)
                                else       -> Color(0xFFEF9A9A)
                            },
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )

                        Spacer(Modifier.height(16.dp))
                        Text(
                            when {
                                taux >= 75 -> "✅ Présence satisfaisante"
                                taux >= 50 -> "⚠️ Taux limite — à surveiller"
                                else       -> "🚨 Taux insuffisant — risque d'exclusion"
                            },
                            color    = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── Statistiques ───────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(Modifier.weight(1f), "Présent",  "$nbPresent",  VertPresent)
                    StatCard(Modifier.weight(1f), "Absent",   "$nbAbsent",   RougeAbsent)
                    StatCard(Modifier.weight(1f), "Séances",  "${seancesEtudiant.size}", BleuClair)
                }
            }

            // ── Titre liste ────────────────────────────────────────────
            item {
                Text("Historique par séance",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            // ── Liste séances ──────────────────────────────────────────
            items(
                seancesEtudiant.sortedByDescending { it.first.date }
            ) { (seance, present) ->
                val dateStr = SimpleDateFormat("dd MMM yyyy — HH:mm", Locale.FRANCE)
                    .format(Date(seance.date))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = if (present) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (present) VertPresent else RougeAbsent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (present) "✓" else "✗",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(seance.matiereNom,
                                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(seance.enseignantNom, fontSize = 12.sp, color = Color.Gray)
                            Text(dateStr, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
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
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
