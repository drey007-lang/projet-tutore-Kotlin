package com.example.tp_b2a.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Enseignant
import com.example.tp_b2a.data.Etudiant
import com.example.tp_b2a.ui.MainViewModel
import com.example.tp_b2a.ui.UserProfile
import com.example.tp_b2a.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnseignantScreen(onRetour: () -> Unit, viewModel: MainViewModel = viewModel()) {

    var valide by remember { mutableStateOf(false) }
    val currentUser = viewModel.currentUserProfile

    when {
        currentUser !is UserProfile.Teacher -> {
            LoginScreen(
                titre = "Enseignant",
                emoji = "👨‍🏫",
                couleur = Color(0xFFC2185B), // Deep Pink
                onRetour = onRetour,
                onConnexion = { nom, code ->
                    viewModel.loginTeacher(nom, code)
                }
            )
        }
        !valide -> {
            EnseignantStatsScreen(
                enseignant = Enseignant(
                    currentUser.entity.id,
                    currentUser.entity.nom,
                    currentUser.entity.prenom,
                    currentUser.entity.matiere,
                    currentUser.entity.code
                ),
                onRetour = { 
                    viewModel.logout()
                    onRetour()
                },
                onValider = {
                    viewModel.saveAttendance(DataSource.etudiants)
                    valide = true
                },
                viewModel = viewModel
            )
        }
        else -> {
            ConfirmationScreen(
                message = "Séance validée !",
                detail = "Les présences ont été enregistrées avec succès.",
                couleur = Color(0xFFC2185B), // Deep Pink
                onRetour = onRetour
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnseignantStatsScreen(
    enseignant: Enseignant,
    onRetour: () -> Unit,
    onValider: () -> Unit,
    viewModel: MainViewModel
) {
    val etudiants = DataSource.etudiants
    val nbPresents = etudiants.count { it.estPresent }
    val nbRetards  = etudiants.count { it.estEnRetard }
    val nbAbsents  = etudiants.count { !it.estPresent && !it.estEnRetard }
    val total      = etudiants.size
    val tauxPresence = if (total > 0) ((nbPresents + nbRetards) * 100 / total) else 0

    var etudiantAleatoire by remember { mutableStateOf<Etudiant?>(null) }
    var showRandomDialog by remember { mutableStateOf(false) }

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
                        Icon(Icons.Default.ExitToApp, contentDescription = "Déconnexion", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.exportAttendanceToPdf(enseignant.matiere, "${enseignant.prenom} ${enseignant.nom}")
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Exporter PDF", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val presents = etudiants.filter { it.estPresent }
                        if (presents.isNotEmpty()) {
                            etudiantAleatoire = presents.random()
                            showRandomDialog = true
                        }
                    }) {
                        Icon(Icons.Default.Casino, contentDescription = "Tirage au sort", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RosePrimaire,
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

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCardEnseignant(Modifier.weight(1f), "Présents",  "$nbPresents",      VertPresent)
                    StatCardEnseignant(Modifier.weight(1f), "Retards",   "$nbRetards",       OrangeRetard)
                    StatCardEnseignant(Modifier.weight(1f), "Absents",   "$nbAbsents",       RougeAbsent)
                }
            }

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

            items(etudiants) { etudiant ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            etudiant.estPresent -> Color(0xFFE8F5E9)
                            etudiant.estEnRetard -> Color(0xFFFFF3E0)
                            else -> Color(0xFFFFEBEE)
                        }
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
                                    when {
                                        etudiant.estPresent -> VertPresent
                                        etudiant.estEnRetard -> OrangeRetard
                                        etudiant.justificatif != null -> Color.Gray
                                        else -> RougeAbsent
                                    },
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
                        Column(Modifier.weight(1f)) {
                            Text(
                                "${etudiant.prenom} ${etudiant.nom}",
                                fontWeight = FontWeight.Medium
                            )
                            if (etudiant.justificatif != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, Modifier.size(14.dp), tint = Color.Gray)
                                    Text(" Justifié (${if (etudiant.estEnRetard) "Retard" else "Absence"})", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when {
                                etudiant.estPresent -> VertPresent
                                etudiant.estEnRetard -> OrangeRetard
                                etudiant.justificatif != null -> Color.Gray
                                else -> RougeAbsent
                            }
                        ) {
                            Text(
                                text = when {
                                    etudiant.estPresent -> "✓ Présent"
                                    etudiant.estEnRetard -> "Retard"
                                    etudiant.justificatif != null -> "Justifié"
                                    else -> "✗ Absent"
                                },
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

    if (showRandomDialog && etudiantAleatoire != null) {
        AlertDialog(
            onDismissRequest = { showRandomDialog = false },
            confirmButton = {
                TextButton(onClick = { showRandomDialog = false }) {
                    Text("Super !", color = RosePrimaire)
                }
            },
            title = { Text("Appel Aléatoire 🎲") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("L'étudiant choisi pour répondre est :", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "${etudiantAleatoire!!.prenom} ${etudiantAleatoire!!.nom}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = RosePrimaire,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }
}

@Composable
fun StatCardEnseignant(modifier: Modifier = Modifier, label: String, valeur: String, couleur: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = couleur),
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
