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
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Delegue
import com.example.tp_b2a.data.Etudiant

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp_b2a.ui.MainViewModel
import com.example.tp_b2a.ui.UserProfile
import com.example.tp_b2a.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueScreen(onRetour: () -> Unit, viewModel: MainViewModel = viewModel()) {

    var valide by remember { mutableStateOf(false) }
    val currentUser = viewModel.currentUserProfile

    if (currentUser !is UserProfile.Delegate) {
        LoginScreen(
            titre = "Délégué",
            emoji = "📌",
            couleur = RosePrimaire,
            onRetour = onRetour,
            onConnexion = { nom, code ->
                viewModel.loginDelegate(nom, code)
            }
        )
    } else if (!valide) {
        DelegueTableauBord(
            delegue = Delegue(currentUser.entity.id, currentUser.entity.nom, currentUser.entity.prenom, currentUser.entity.code),
            onRetour = onRetour,
            onValider = { valide = true }
        )
    } else {
        ConfirmationScreen(
            message = "Liste validée !",
            detail = "La fiche de présence a été transmise à l'enseignant.",
            couleur = RosePrimaire,
            onRetour = onRetour
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueTableauBord(
    delegue: Delegue,
    onRetour: () -> Unit,
    onValider: () -> Unit
) {
    var etudiantsLocal by remember { mutableStateOf(DataSource.etudiants.toList()) }
    var showJustifDialog by remember { mutableStateOf<Pair<Etudiant, Boolean>?>(null) }

    val nbPresents = etudiantsLocal.count { it.estPresent }
    val nbRetards  = etudiantsLocal.count { it.estEnRetard }
    val nbAbsents  = etudiantsLocal.count { !it.estPresent && !it.estEnRetard }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Vérification", fontWeight = FontWeight.Bold)
                        Text("Délégué : ${delegue.prenom} ${delegue.nom}", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
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
                    onClick = {
                        DataSource.etudiants.clear()
                        DataSource.etudiants.addAll(etudiantsLocal)
                        onValider()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimaire)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Valider et Envoyer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GrisFond)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCardDelegue(modifier = Modifier.weight(1f), label = "Présents", valeur = "$nbPresents", couleur = VertPresent)
                StatCardDelegue(modifier = Modifier.weight(1f), label = "Retards", valeur = "$nbRetards", couleur = OrangeRetard)
                StatCardDelegue(modifier = Modifier.weight(1f), label = "Absents", valeur = "$nbAbsents", couleur = RougeAbsent)
            }

            Text(
                "Appuyez pour présent, horloge pour retard, dossier pour justificatif",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(etudiantsLocal) { etudiant ->
                    Card(
                        onClick = {
                            etudiantsLocal = etudiantsLocal.map { e ->
                                if (e.id == etudiant.id) {
                                    val newPresent = !e.estPresent
                                    e.copy(estPresent = newPresent, estEnRetard = if (newPresent) false else e.estEnRetard)
                                } else e
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                etudiant.estPresent -> Color(0xFFE8F5E9)
                                etudiant.estEnRetard -> Color(0xFFFFF3E0)
                                else -> Color(0xFFFFEBEE)
                            }
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
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${etudiant.prenom} ${etudiant.nom}", fontWeight = FontWeight.Medium)
                                if (etudiant.justificatif != null) {
                                    Text("Justifié (${if (etudiant.estEnRetard) "Retard" else "Absence"})", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            // Bouton Retard
                            if (!etudiant.estPresent) {
                                IconButton(onClick = {
                                    if (!etudiant.estEnRetard) {
                                        showJustifDialog = Pair(etudiant, true)
                                    } else {
                                        etudiantsLocal = etudiantsLocal.map { e ->
                                            if (e.id == etudiant.id) e.copy(estEnRetard = false) else e
                                        }
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = "Marquer retard",
                                        tint = if (etudiant.estEnRetard) OrangeRetard else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Bouton justificatif si absent pur
                            if (!etudiant.estPresent && !etudiant.estEnRetard) {
                                IconButton(onClick = { showJustifDialog = Pair(etudiant, false) }) {
                                    Icon(
                                        Icons.Default.Assignment,
                                        contentDescription = "Ajouter justificatif",
                                        tint = if (etudiant.justificatif != null) VertPresent else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
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
                                        etudiant.estPresent -> "Présent"
                                        etudiant.estEnRetard -> "Retard"
                                        etudiant.justificatif != null -> "Justifié"
                                        else -> "Absent"
                                    },
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

    if (showJustifDialog != null) {
        val (etudiant, isLate) = showJustifDialog!!
        AlertDialog(
            onDismissRequest = { showJustifDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    etudiantsLocal = etudiantsLocal.map { e ->
                        if (e.id == etudiant.id) {
                            if (isLate) e.copy(estEnRetard = true, estPresent = false, justificatif = "délégué_retard")
                            else e.copy(justificatif = "délégué_absence")
                        } else e
                    }
                    showJustifDialog = null
                }) { Text("Confirmer") }
            },
            dismissButton = {
                TextButton(onClick = { showJustifDialog = null }) { Text("Annuler") }
            },
            title = { Text(if (isLate) "Justifier le Retard" else "Justifier l'absence") },
            text = { Text("Confirmez-vous avoir reçu un justificatif pour le ${if (isLate) "retard" else "absence"} de ${etudiant.prenom} ${etudiant.nom} ?") }
        )
    }
}

@Composable
fun StatCardDelegue(modifier: Modifier = Modifier, label: String, valeur: String, couleur: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = couleur),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valeur, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}
