package com.example.tp_b2a.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.model.Seance
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.PresenceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Écran d'historique — 2 onglets ───────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoriqueScreen(
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit,
    onEtudiantClick: (Int) -> Unit
) {
    BackHandler { onRetour() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val seances = presenceViewModel.seances
    val etudiants = presenceViewModel.etudiants

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Onglets
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = BleuPrimaire,
                contentColor     = Color.White,
                indicator        = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier  = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color     = Color.White
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Séances (${seances.size})")
                        }
                    },
                    selectedContentColor   = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Étudiants (${etudiants.size})")
                        }
                    },
                    selectedContentColor   = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
            }

            when (selectedTab) {
                0 -> OngletSeances(seances)
                1 -> OngletEtudiants(
                    etudiants  = etudiants,
                    viewModel  = presenceViewModel,
                    onEtudiantClick = onEtudiantClick
                )
            }
        }
    }
}

// ─── Onglet Séances ───────────────────────────────────────────────────────

@Composable
private fun OngletSeances(seances: List<Seance>) {
    if (seances.isEmpty()) {
        EmptyState(
            emoji   = "📭",
            message = "Aucune séance enregistrée",
            detail  = "Les séances apparaîtront ici après validation par un enseignant."
        )
        return
    }

    // Résumé global
    val totalSeances   = seances.size
    val tauxGlobal     = if (seances.isNotEmpty())
        seances.sumOf { it.taux } / seances.size else 0

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Résumé
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = BleuPrimaire)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ResumeItem("Séances",    "$totalSeances", Color.White)
                    ResumeItem("Taux moyen", "$tauxGlobal %", Color.White)
                }
            }
        }

        // Liste des séances
        items(seances.sortedByDescending { it.date }) { seance ->
            SeanceCard(seance)
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ResumeItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 12.sp, color = color.copy(alpha = 0.8f))
    }
}

@Composable
private fun SeanceCard(seance: Seance) {
    val dateStr = SimpleDateFormat("dd MMM yyyy — HH:mm", Locale.FRANCE)
        .format(Date(seance.date))

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(seance.matiereNom,
                        fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(seance.enseignantNom,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp)
                    Text(dateStr,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp))
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        seance.taux >= 75 -> VertPresent
                        seance.taux >= 50 -> Color(0xFFE65100)
                        else              -> RougeAbsent
                    }
                ) {
                    Text(
                        "${seance.taux} %",
                        color    = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Barre de progression
            LinearProgressIndicator(
                progress   = { seance.taux / 100f },
                modifier   = Modifier.fillMaxWidth().height(6.dp),
                color      = when {
                    seance.taux >= 75 -> VertPresent
                    seance.taux >= 50 -> Color(0xFFE65100)
                    else              -> RougeAbsent
                },
                trackColor = Color(0xFFEEEEEE)
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipStat("✓ ${seance.nbPresents} présents",   VertPresent)
                ChipStat("✗ ${seance.nbAbsents} absents",    RougeAbsent)
            }
        }
    }
}

@Composable
private fun ChipStat(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text     = text,
            color    = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ─── Onglet Étudiants ─────────────────────────────────────────────────────

@Composable
private fun OngletEtudiants(
    etudiants: List<com.example.tp_b2a.data.model.Etudiant>,
    viewModel: PresenceViewModel,
    onEtudiantClick: (Int) -> Unit
) {
    if (viewModel.seances.isEmpty()) {
        EmptyState(
            emoji   = "📊",
            message = "Pas encore de données",
            detail  = "Les statistiques par étudiant apparaîtront après la première séance validée."
        )
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(etudiants, key = { it.id }) { etudiant ->
            val taux = viewModel.tauxPresenceEtudiant(etudiant.id)
            EtudiantStatItem(
                etudiant = etudiant,
                taux     = taux,
                onClick  = { onEtudiantClick(etudiant.id) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun EtudiantStatItem(
    etudiant: com.example.tp_b2a.data.model.Etudiant,
    taux: Int,
    onClick: () -> Unit
) {
    val couleur = when {
        taux >= 75 -> VertPresent
        taux >= 50 -> Color(0xFFE65100)
        else       -> RougeAbsent
    }

    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(couleur, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(etudiant.initiales, color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(etudiant.nomComplet, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress   = { taux / 100f },
                    modifier   = Modifier.fillMaxWidth().height(5.dp),
                    color      = couleur,
                    trackColor = Color(0xFFEEEEEE)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "$taux %",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = couleur
            )
            Spacer(Modifier.width(4.dp))
            Text("›", fontSize = 20.sp, color = Color.LightGray)
        }
    }
}

// ─── État vide ────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(emoji: String, message: String, detail: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Text(emoji, fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(message, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Text(detail, fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                textAlign = TextAlign.Center)
        }
    }
}
