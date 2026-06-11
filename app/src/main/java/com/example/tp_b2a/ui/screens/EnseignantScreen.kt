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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Enseignant
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.utils.PdfGenerator
import com.example.tp_b2a.viewmodel.AuthViewModel
import com.example.tp_b2a.viewmodel.PresenceViewModel

private val CouleurEnseignant = Color(0xFF00695C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnseignantScreen(
    presenceViewModel: PresenceViewModel,
    authViewModel: AuthViewModel,
    onRetour: () -> Unit,
    onHistorique: () -> Unit
) {
    BackHandler { onRetour() }

    val authState     by authViewModel.uiState.collectAsStateWithLifecycle()

    var enseignantSelectionne by remember { mutableStateOf<Enseignant?>(null) }
    var valide by remember { mutableStateOf(false) }

    when {
        authState.enseignantConnecte == null -> {
            LoginScreen(
                titre    = "Enseignant",
                emoji    = "👨‍🏫",
                couleur  = CouleurEnseignant,
                onRetour = onRetour,
                onConnexion = { code ->
                    !authViewModel.connecterEnseignant(code)
                }
            )
        }
        enseignantSelectionne == null -> {
            ListeEnseignantsScreen(
                onRetour = {
                    authViewModel.deconnecter()
                    onRetour()
                },
                onSelectEnseignant = { enseignantSelectionne = it }
            )
        }
        !valide -> {
            EnseignantStatsScreen(
                enseignant        = enseignantSelectionne!!,
                presenceViewModel = presenceViewModel,
                onRetour          = { enseignantSelectionne = null },
                onValider         = { valide = true },
                onHistorique      = onHistorique
            )
        }
        else -> {
            ConfirmationScreen(
                message  = "Séance validée !",
                detail   = "Les présences ont été enregistrées avec succès.",
                couleur  = CouleurEnseignant,
                onRetour = {
                    presenceViewModel.nouvelleSeance()
                    authViewModel.deconnecter()
                    onRetour()
                }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = CouleurEnseignant,
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
            Text(
                "Sélectionnez votre matière",
                modifier = Modifier.padding(16.dp),
                color    = Color.Gray,
                fontSize = 14.sp
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DataSource.enseignants) { enseignant ->
                    Card(
                        onClick   = { onSelectEnseignant(enseignant) },
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    .background(CouleurEnseignant, CircleShape),
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
                                Text("${enseignant.prenom} ${enseignant.nom}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(enseignant.matiere, color = Color.Gray, fontSize = 13.sp)
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
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit,
    onValider: () -> Unit,
    onHistorique: () -> Unit
) {
    val context  = LocalContext.current
    val state    by presenceViewModel.uiState.collectAsStateWithLifecycle()
    val etudiants = state.etudiants
    val nbPresents  = state.presences.values.count { it }
    val nbAbsents   = etudiants.size - nbPresents
    val total       = etudiants.size
    val tauxPresence = if (total > 0) (nbPresents * 100 / total) else 0

    var pdfMessage by remember { mutableStateOf<String?>(null) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onHistorique) {
                        Icon(Icons.Default.History, contentDescription = "Historique", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val fichier = PdfGenerator.genererPdf(
                            context       = context,
                            enseignant    = enseignant,
                            etudiants     = etudiants,
                            presencesMap  = state.presences
                        )
                        pdfMessage = if (fichier != null)
                            "PDF sauvegardé : ${fichier.name}"
                        else
                            "Erreur lors de la génération du PDF"
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Exporter PDF", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = CouleurEnseignant,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        presenceViewModel.confirmerSeance(enseignant.matiere, "${enseignant.prenom} ${enseignant.nom}")
                        onValider()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CouleurEnseignant)
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
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Message PDF ──────────────────────────────────────
            if (pdfMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Text(
                            pdfMessage!!,
                            modifier = Modifier.padding(16.dp),
                            color    = VertPresent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── Carte enseignant ─────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = CouleurEnseignant)
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
                    StatCard(Modifier.weight(1f), "Présents",  "$nbPresents",     VertPresent)
                    StatCard(Modifier.weight(1f), "Absents",   "$nbAbsents",      RougeAbsent)
                    StatCard(Modifier.weight(1f), "Taux",      "$tauxPresence %", BleuClair)
                }
            }

            // ── Barre de progression ──────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taux de présence", fontWeight = FontWeight.Medium)
                            Text("$tauxPresence %", fontWeight = FontWeight.Bold, color = BleuClair)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress    = { tauxPresence / 100f },
                            modifier    = Modifier.fillMaxWidth().height(10.dp),
                            color       = VertPresent,
                            trackColor  = Color(0xFFEEEEEE)
                        )
                    }
                }
            }

            // ── Titre liste ───────────────────────────────────────
            item {
                Text("Détail par étudiant",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = MaterialTheme.colorScheme.onBackground
                )
            }

            // ── Liste étudiants ───────────────────────────────────
            items(etudiants) { etudiant ->
                val estPresent = state.presences[etudiant.id] ?: false
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(10.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (estPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
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
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("${etudiant.prenom} ${etudiant.nom}", Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (estPresent) VertPresent else RougeAbsent
                        ) {
                            Text(
                                if (estPresent) "✓ Présent" else "✗ Absent",
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
