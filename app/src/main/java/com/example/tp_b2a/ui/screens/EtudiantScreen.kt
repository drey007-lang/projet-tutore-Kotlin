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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tp_b2a.data.Etudiant
import com.example.tp_b2a.ui.theme.*
import com.example.tp_b2a.viewmodel.PresenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtudiantScreen(
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit
) {
    BackHandler { onRetour() }

    val state by presenceViewModel.uiState.collectAsStateWithLifecycle()
    var recherche by remember { mutableStateOf("") }
    var confirme  by remember { mutableStateOf(false) }

    val etudiantsFiltres = state.etudiants.filter {
        it.nom.contains(recherche, ignoreCase = true) ||
                it.prenom.contains(recherche, ignoreCase = true)
    }
    val nbPresents = state.presences.values.count { it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Liste des étudiants", fontWeight = FontWeight.Bold)
                        Text(
                            "$nbPresents présent(s) sur ${state.etudiants.size}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
            AnimatedVisibility(
                visible = !confirme,
                enter = slideInVertically(initialOffsetY = { it }),
                exit  = slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = {
                            presenceViewModel.confirmerSeance("—", "Étudiant")
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

        AnimatedContent(
            targetState = confirme,
            transitionSpec = {
                fadeIn() + slideInHorizontally() togetherWith fadeOut()
            }
        ) { isConfirme ->
            if (isConfirme) {
                // ── Écran de confirmation ───────────────────────────
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
                // ── Liste des étudiants ─────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
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
                            focusedBorderColor   = BleuClair,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(etudiantsFiltres, key = { it.id }) { etudiant ->
                            val estPresent = state.presences[etudiant.id] ?: false
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically()
                            ) {
                                EtudiantItem(
                                    etudiant   = etudiant,
                                    estPresent = estPresent,
                                    onToggle   = { presenceViewModel.togglePresence(etudiant.id) }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun EtudiantItem(
    etudiant: Etudiant,
    estPresent: Boolean,
    onToggle: () -> Unit
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estPresent) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (estPresent) VertPresent else BleuClair,
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
                    text = if (estPresent) "✓ Présent(e)" else "Appuyer pour marquer présent",
                    fontSize = 13.sp,
                    color = if (estPresent) VertPresent else Color.Gray
                )
            }

            Checkbox(
                checked = estPresent,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor   = VertPresent,
                    uncheckedColor = Color.LightGray
                )
            )
        }
    }
}
