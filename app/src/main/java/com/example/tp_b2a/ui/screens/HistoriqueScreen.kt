package com.example.tp_b2a.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.PresenceViewModel
import com.example.tp_b2a.viewmodel.SeanceAvecPresences

// ─── Écran historique des séances ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoriqueScreen(
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit
) {
    BackHandler { onRetour() }

    val state by presenceViewModel.uiState.collectAsStateWithLifecycle()
    val historique = state.seancesHistorique
    val etudiants  = state.etudiants

    var tabSelectionne by remember { mutableIntStateOf(0) }
    val tabs = listOf("Séances", "Par étudiant")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Historique", fontWeight = FontWeight.Bold)
                        Text("${historique.size} séance(s)", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Onglets ──────────────────────────────────────────
            TabRow(
                selectedTabIndex = tabSelectionne,
                containerColor   = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, titre ->
                    Tab(
                        selected = tabSelectionne == index,
                        onClick  = { tabSelectionne = index },
                        text     = { Text(titre, fontWeight = if (tabSelectionne == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            AnimatedContent(
                targetState = tabSelectionne,
                transitionSpec = { fadeIn() + slideInHorizontally() togetherWith fadeOut() }
            ) { tab ->
                when (tab) {
                    0 -> OngletSeances(historique)
                    1 -> OngletEtudiants(etudiants, historique, presenceViewModel)
                }
            }
        }
    }
}

// ─── Onglet : liste des séances ──────────────────────────────────────────────

@Composable
fun OngletSeances(historique: List<SeanceAvecPresences>) {
    if (historique.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📋", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text("Aucune séance enregistrée", fontSize = 18.sp, color = Color.Gray)
                Text("Validez une séance pour voir l'historique", fontSize = 14.sp, color = Color.Gray)
            }
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(historique.reversed()) { record ->
            SeanceCard(record)
        }
    }
}

@Composable
fun SeanceCard(record: SeanceAvecPresences) {
    val nbPresents = record.presences.values.count { it }
    val total      = record.presences.size
    val taux       = if (total > 0) nbPresents * 100 / total else 0

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(record.seance.matiereNom, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(record.seance.enseignantNom, fontSize = 13.sp, color = Color.Gray)
                    Text(record.seance.date.toString(), fontSize = 12.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            when {
                                taux >= 75 -> VertPresent
                                taux >= 50 -> Color(0xFFF57F17)
                                else       -> RougeAbsent
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$taux%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress   = { taux / 100f },
                modifier   = Modifier.fillMaxWidth().height(6.dp),
                color      = VertPresent,
                trackColor = Color(0xFFEEEEEE)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "$nbPresents présent(s) / $total",
                fontSize = 12.sp,
                color    = Color.Gray
            )
        }
    }
}

// ─── Onglet : stats par étudiant ─────────────────────────────────────────────

@Composable
fun OngletEtudiants(
    etudiants: List<com.example.tp_b2a.data.Etudiant>,
    historique: List<SeanceAvecPresences>,
    presenceViewModel: PresenceViewModel
) {
    if (historique.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucune donnée disponible", color = Color.Gray)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Graphique global
        item {
            GraphiquePresences(etudiants, historique)
        }
        item {
            Text("Détail par étudiant", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 8.dp))
        }
        items(etudiants) { etudiant ->
            val taux = presenceViewModel.tauxPresenceEtudiant(etudiant.id)
            EtudiantStatItem(etudiant, taux)
        }
    }
}

@Composable
fun EtudiantStatItem(etudiant: com.example.tp_b2a.data.Etudiant, taux: Float) {
    val tauxPct = (taux * 100).toInt()
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BleuClair, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${etudiant.prenom.first()}${etudiant.nom.first()}",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${etudiant.prenom} ${etudiant.nom}", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress   = { taux },
                    modifier   = Modifier.fillMaxWidth().height(6.dp),
                    color      = when {
                        tauxPct >= 75 -> VertPresent
                        tauxPct >= 50 -> OrangeRetard
                        else          -> RougeAbsent
                    },
                    trackColor = Color(0xFFEEEEEE)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "$tauxPct%",
                fontWeight = FontWeight.Bold,
                color = when {
                    tauxPct >= 75 -> VertPresent
                    tauxPct >= 50 -> OrangeRetard
                    else          -> RougeAbsent
                }
            )
        }
    }
}

// ─── Graphique en barres (Canvas natif) ──────────────────────────────────────

@Composable
fun GraphiquePresences(
    etudiants: List<com.example.tp_b2a.data.Etudiant>,
    historique: List<SeanceAvecPresences>
) {
    val tauxParSeance = historique.map { record ->
        val presents = record.presences.values.count { it }
        val total    = record.presences.size
        if (total > 0) presents.toFloat() / total else 0f
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Taux de présence par séance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            if (tauxParSeance.isEmpty()) {
                Text("Aucune donnée", color = Color.Gray)
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    val barWidth  = size.width / (tauxParSeance.size * 1.5f)
                    val spacing   = barWidth * 0.5f
                    val maxHeight = size.height - 20f

                    tauxParSeance.forEachIndexed { i, taux ->
                        val barHeight = taux * maxHeight
                        val x = i * (barWidth + spacing)
                        val y = maxHeight - barHeight + 10f
                        val couleur = when {
                            taux >= 0.75f -> VertPresent
                            taux >= 0.50f -> Color(0xFFF57F17)
                            else          -> RougeAbsent
                        }
                        drawRoundRect(
                            color        = couleur,
                            topLeft      = Offset(x, y),
                            size         = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4f)
                        )
                    }
                }
            }
        }
    }
}
