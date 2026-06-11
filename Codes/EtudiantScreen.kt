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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Etudiant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtudiantScreen(onRetour: () -> Unit) {

    // État local des présences (copie mutable pour recomposition)
    var etudiants by remember {
        mutableStateOf(DataSource.etudiants.toList())
    }
    var recherche by remember { mutableStateOf("") }
    var confirme by remember { mutableStateOf(false) }

    // Filtre par recherche
    val etudiantsFiltres = etudiants.filter {
        it.nom.contains(recherche, ignoreCase = true) ||
        it.prenom.contains(recherche, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Liste des étudiants", fontWeight = FontWeight.Bold)
                        Text(
                            "${etudiants.count { it.estPresent }} présent(s) sur ${etudiants.size}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BleuPrimaire,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Bouton de confirmation en bas
            if (!confirme) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = {
                            // Enregistre dans DataSource et confirme
                            DataSource.etudiants.forEachIndexed { i, e ->
                                e.estPresent = etudiants[i].estPresent
                            }
                            confirme = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VertPresent)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Confirmer ma présence", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->

        if (confirme) {
            // ── Écran de confirmation ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(VertPresent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Présence enregistrée !", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VertPresent)
                    Spacer(Modifier.height(8.dp))
                    Text("Votre présence a bien été prise en compte.", color = Color.Gray)
                    Spacer(Modifier.height(32.dp))
                    OutlinedButton(onClick = onRetour) {
                        Text("Retour à l'accueil")
                    }
                }
            }
        } else {
            // ── Liste des étudiants ────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(GrisFond)
            ) {
                // Barre de recherche
                OutlinedTextField(
                    value = recherche,
                    onValueChange = { recherche = it },
                    placeholder = { Text("Rechercher mon nom...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BleuClair,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = BlancCard,
                        unfocusedContainerColor = BlancCard
                    )
                )

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(etudiantsFiltres, key = { it.id }) { etudiant ->
                        EtudiantItem(
                            etudiant = etudiant,
                            onToggle = {
                                // Met à jour la présence dans la liste locale
                                etudiants = etudiants.map { e ->
                                    if (e.id == etudiant.id) e.copy(estPresent = !e.estPresent) else e
                                }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun EtudiantItem(etudiant: Etudiant, onToggle: () -> Unit) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (etudiant.estPresent) Color(0xFFE8F5E9) else BlancCard
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar initiales
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (etudiant.estPresent) VertPresent else BleuClair,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${etudiant.prenom.first()}${etudiant.nom.first()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${etudiant.prenom} ${etudiant.nom}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (etudiant.estPresent) "✓ Présent(e)" else "Appuyer pour marquer présent",
                    fontSize = 13.sp,
                    color = if (etudiant.estPresent) VertPresent else Color.Gray
                )
            }

            // Case à cocher
            Checkbox(
                checked = etudiant.estPresent,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = VertPresent,
                    uncheckedColor = Color.LightGray
                )
            )
        }
    }
}
