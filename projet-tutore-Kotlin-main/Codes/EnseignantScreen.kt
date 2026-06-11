package com.example.tp_b2a.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Enseignant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnseignantScreen(onRetour: () -> Unit) {

    var enseignantConnecte by remember { mutableStateOf<Enseignant?>(null) }
    var enseignantSelectionne by remember { mutableStateOf<Enseignant?>(null) }
    var valide by remember { mutableStateOf(false) }

    when {
        // ── 1. Connexion ──────────────────────────────────────────
        enseignantConnecte == null -> {
            LoginScreen(
                titre = "Enseignant",
                emoji = "👨‍🏫",
                couleur = Color(0xFF00695C),
                onRetour = onRetour,
                onConnexion = { code ->
                    enseignantConnecte = DataSource.enseignants.find { it.code == code }
                    enseignantConnecte == null
                }
            )
        }
        // ── 2. Liste des enseignants ───────────────────────────────
        enseignantSelectionne == null -> {
            ListeEnseignantsScreen(
                onRetour = onRetour,
                onSelectEnseignant = { enseignantSelectionne = it }
            )
        }
        // ── 3. Statistiques de présence ───────────────────────────
        !valide -> {
            EnseignantStatsScreen(
                enseignant = enseignantSelectionne!!,
                onRetour = { enseignantSelectionne = null },
                onValider = { valide = true }
            )
        }
        // ── 4. Confirmation ───────────────────────────────────────
        else -> {
            ConfirmationScreen(
                message = "Séance validée !",
                detail = "Les présences ont été enregistrées avec succès.",
                couleur = Color(0xFF00695C),
                onRetour = onRetour
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeEnseignantsScreen(
    onRetour: () -> Unit,
    onSelectEnseignant: (Enseignant) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choisir un cours", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00695C),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GrisFond)
        ) {
            Text(
                "Sélectionnez votre matière",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DataSource.enseignants) { enseignant ->
                    Card(
                        onClick = { onSelectEnseignant(enseignant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancCard),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFF00695C), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${enseignant.prenom.first()}${enseignant.nom.first()}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "${enseignant.prenom} ${enseignant.nom}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    enseignant.matiere,
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                            Text("›", fontSize = 28.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnseignantStatsScreen(
    enseignant: Enseignant,
    onRetour: () -> Unit,
    onValider: () -> Unit
) {
    val etudiants = DataSource.etudiants
    val nbPresents = etudiants.count { it.estPresent }
    val nbAbsents  = etudiants.count { !it.estPresent }
    val total      = etudiants.size
    val tauxPresence = if (total > 0) (nbPresents * 100 / total) else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Récapitulatif", fontWeight = FontWeight.Bold)
                        Text(enseignant.matiere, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00695C),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Valider la séance", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GrisFond),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Carte enseignant ─────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00695C))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👨‍🏫", fontSize = 36.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("${enseignant.prenom} ${enseignant.nom}",
                                fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Text(enseignant.matiere, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                        }
                    }
                }
            }

            // ── Statistiques ──────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(Modifier.weight(1f), "Présents",  "$nbPresents",      VertPresent)
                    StatCard(Modifier.weight(1f), "Absents",   "$nbAbsents",       RougeAbsent)
                    StatCard(Modifier.weight(1f), "Taux",      "$tauxPresence %",  BleuClair)
                }
            }

            // ── Barre de progression ──────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancCard)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Taux de présence", fontWeight = FontWeight.Medium)
                            Text("$tauxPresence %", fontWeight = FontWeight.Bold, color = BleuClair)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { tauxPresence / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = VertPresent,
                            trackColor = Color(0xFFEEEEEE)
                        )
                    }
                }
            }

            // ── Titre liste ───────────────────────────────────────
            item {
                Text("Détail par étudiant",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }

            // ── Liste étudiants en lecture seule ──────────────────
            items(etudiants) { etudiant ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (etudiant.estPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (etudiant.estPresent) VertPresent else RougeAbsent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${etudiant.prenom.first()}${etudiant.nom.first()}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "${etudiant.prenom} ${etudiant.nom}",
                            Modifier.weight(1f),
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (etudiant.estPresent) VertPresent else RougeAbsent
                        ) {
                            Text(
                                if (etudiant.estPresent) "✓ Présent" else "✗ Absent",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
