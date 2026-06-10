package com.example.tp_b2a.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Delegue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueScreen(onRetour: () -> Unit) {

    var delegueConnecte by remember { mutableStateOf<Delegue?>(null) }
    var valide by remember { mutableStateOf(false) }

    if (delegueConnecte == null) {
        // ── Écran de connexion ─────────────────────────────────────
        LoginScreen(
            titre = "Délégué",
            emoji = "📌",
            couleur = Color(0xFF6A1B9A),
            onRetour = onRetour,
            onConnexion = { code ->
                delegueConnecte = DataSource.delegues.find { it.code == code }
                delegueConnecte == null // renvoie true si erreur
            }
        )
    } else if (!valide) {
        // ── Tableau de bord délégué ────────────────────────────────
        DelegueTableauBord(
            delegue = delegueConnecte!!,
            onRetour = onRetour,
            onValider = { valide = true }
        )
    } else {
        // ── Confirmation validation ────────────────────────────────
        ConfirmationScreen(
            message = "Liste validée !",
            detail = "La fiche de présence a été transmise à l'enseignant.",
            couleur = Color(0xFF6A1B9A),
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
    val etudiants = DataSource.etudiants

    val nbPresents = etudiants.count { it.estPresent }
    val nbAbsents  = etudiants.count { !it.estPresent }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tableau de bord", fontWeight = FontWeight.Bold)
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
                    containerColor = Color(0xFF6A1B9A),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
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
                .background(GrisFond)
        ) {
            // ── Résumé statistiques ──────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Présents",
                    valeur = "$nbPresents",
                    couleur = VertPresent
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Absents",
                    valeur = "$nbAbsents",
                    couleur = RougeAbsent
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Total",
                    valeur = "${etudiants.size}",
                    couleur = BleuClair
                )
            }

            Text(
                "Liste de présence",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(etudiants) { etudiant ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (etudiant.estPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
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
                                        if (etudiant.estPresent) VertPresent else RougeAbsent,
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
                            // Badge statut
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (etudiant.estPresent) VertPresent else RougeAbsent
                            ) {
                                Text(
                                    text = if (etudiant.estPresent) "Présent" else "Absent",
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
