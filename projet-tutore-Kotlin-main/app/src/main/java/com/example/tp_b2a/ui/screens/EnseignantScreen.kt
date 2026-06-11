package com.example.tp_b2a.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.model.Enseignant
import com.example.tp_b2a.data.model.Etudiant
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.utils.PdfGenerator
import com.example.tp_b2a.viewmodel.AuthViewModel
import com.example.tp_b2a.viewmodel.PresenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnseignantScreen(
    presenceViewModel: PresenceViewModel,
    authViewModel: AuthViewModel,
    onRetour: () -> Unit,
    onHistoriqueClick: () -> Unit
) {
    var enseignantSelectionne by remember { mutableStateOf<Enseignant?>(null) }
    var valide by remember { mutableStateOf(false) }

    BackHandler { onRetour() }

    when {
        // ── 1. Connexion ──────────────────────────────────────────────
        authViewModel.enseignantConnecte == null -> {
            LoginScreen(
                titre      = "Enseignant",
                emoji      = "👨‍🏫",
                couleur    = CouleurEnseignant,
                onRetour   = onRetour,
                onConnexion = { code -> !authViewModel.connecterEnseignant(code) }
            )
        }

        // ── 2. Choix du cours ─────────────────────────────────────────
        enseignantSelectionne == null -> {
            ChoixCoursScreen(
                enseignantConnecte    = authViewModel.enseignantConnecte!!,
                onRetour              = onRetour,
                onHistoriqueClick     = onHistoriqueClick,
                onSelectEnseignant    = { enseignantSelectionne = it }
            )
        }

        // ── 3. Statistiques ───────────────────────────────────────────
        !valide -> {
            EnseignantStatsScreen(
                enseignant        = enseignantSelectionne!!,
                presenceViewModel = presenceViewModel,
                onRetour          = { enseignantSelectionne = null },
                onValider         = {
                    presenceViewModel.confirmerSeance(enseignantSelectionne!!)
                    valide = true
                }
            )
        }

        // ── 4. Confirmation ───────────────────────────────────────────
        else -> {
            ConfirmationScreen(
                message  = "Séance validée !",
                detail   = "Les présences ont été enregistrées et sauvegardées.",
                couleur  = CouleurEnseignant,
                onRetour = onRetour
            )
        }
    }
}

// ─── Choix du cours ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoixCoursScreen(
    enseignantConnecte: Enseignant,
    onRetour: () -> Unit,
    onHistoriqueClick: () -> Unit,
    onSelectEnseignant: (Enseignant) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choisir un cours", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onHistoriqueClick) {
                        Icon(Icons.Default.History,
                            contentDescription = "Historique", tint = Color.White)
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
            // Info de connexion
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = CouleurEnseignant)
            ) {
                Row(
                    modifier          = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👋", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Bonjour,",
                            color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        Text(enseignantConnecte.nomComplet,
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
            }

            Text(
                "Sélectionnez la matière du cours",
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize   = 13.sp
            )

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DataSource.enseignants) { enseignant ->
                    val isMoi = enseignant.id == enseignantConnecte.id
                    Card(
                        onClick   = { onSelectEnseignant(enseignant) },
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(
                            containerColor = if (isMoi) Color(0xFFE8F5E0) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(
                            modifier          = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(CouleurEnseignant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(enseignant.initiales, color = Color.White,
                                    fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(enseignant.nomComplet,
                                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(enseignant.matiere, color = Color.Gray, fontSize = 13.sp)
                                if (isMoi) Text("(Mon cours)",
                                    color = CouleurEnseignant, fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                            Text("›", fontSize = 28.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

// ─── Statistiques de la séance ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnseignantStatsScreen(
    enseignant: Enseignant,
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit,
    onValider: () -> Unit
) {
    val context     = LocalContext.current
    val etudiants   = presenceViewModel.etudiants
    val nbPresents  = presenceViewModel.nbPresents()
    val nbAbsents   = presenceViewModel.nbAbsents()
    val taux        = presenceViewModel.taux()
    var pdfLoading  by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Récapitulatif", fontWeight = FontWeight.Bold)
                        Text(enseignant.matiere, fontSize = 12.sp,
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
                    // Bouton PDF
                    IconButton(
                        onClick = {
                            pdfLoading = true
                            // Génère et partage un PDF d'aperçu (avant validation)
                            exporterPdfApercu(context, enseignant, etudiants, presenceViewModel)
                            pdfLoading = false
                        }
                    ) {
                        if (pdfLoading) {
                            CircularProgressIndicator(
                                color    = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.PictureAsPdf,
                                contentDescription = "Exporter PDF", tint = Color.White)
                        }
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
                    onClick  = onValider,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CouleurEnseignant)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Valider la séance", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Carte enseignant
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = CouleurEnseignant)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👨‍🏫", fontSize = 36.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(enseignant.nomComplet,
                                fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Text(enseignant.matiere,
                                color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                        }
                    }
                }
            }

            // Stats
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(Modifier.weight(1f), "Présents", "$nbPresents", VertPresent)
                    StatCard(Modifier.weight(1f), "Absents",  "$nbAbsents",  RougeAbsent)
                    StatCard(Modifier.weight(1f), "Taux",     "$taux %",     BleuClair)
                }
            }

            // Barre de progression
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Taux de présence", fontWeight = FontWeight.Medium)
                            Text("$taux %", fontWeight = FontWeight.Bold, color = BleuClair)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress    = { taux / 100f },
                            modifier    = Modifier.fillMaxWidth().height(10.dp),
                            color       = if (taux >= 75) VertPresent else RougeAbsent,
                            trackColor  = Color(0xFFEEEEEE)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (taux >= 75) "✓ Bon taux de présence" else "⚠ Taux insuffisant",
                            fontSize = 12.sp,
                            color    = if (taux >= 75) VertPresent else RougeAbsent
                        )
                    }
                }
            }

            // Titre liste
            item {
                Text("Détail par étudiant",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            // Liste étudiants (lecture seule)
            items(etudiants) { etudiant ->
                val present = presenceViewModel.estPresent(etudiant.id)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = if (present) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(12.dp),
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
                            Text(etudiant.initiales, color = Color.White,
                                fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(etudiant.nomComplet, Modifier.weight(1f),
                            fontWeight = FontWeight.Medium)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (present) VertPresent else RougeAbsent
                        ) {
                            Text(
                                if (present) "✓ Présent" else "✗ Absent",
                                color    = Color.White,
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

// ─── Helper export PDF ────────────────────────────────────────────────────

private fun exporterPdfApercu(
    context: Context,
    enseignant: Enseignant,
    etudiants: List<Etudiant>,
    presenceViewModel: PresenceViewModel
) {
    // Crée une séance temporaire pour l'aperçu PDF
    val seanceApercu = com.example.tp_b2a.data.model.Seance(
        id            = "apercu",
        date          = System.currentTimeMillis(),
        enseignantId  = enseignant.id,
        matiereNom    = enseignant.matiere,
        enseignantNom = enseignant.nomComplet,
        presences     = etudiants.associate { e ->
            e.id.toString() to presenceViewModel.estPresent(e.id)
        }
    )
    PdfGenerator.partagerPdf(context, seanceApercu, etudiants)
}
