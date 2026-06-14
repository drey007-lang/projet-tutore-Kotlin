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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Etudiant
import com.example.tp_b2a.ui.theme.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp_b2a.ui.MainViewModel

import androidx.compose.material.icons.filled.QrCodeScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtudiantScreen(onRetour: () -> Unit, onScanClick: () -> Unit, viewModel: MainViewModel = viewModel()) {

    val dbEtudiants by viewModel.allEtudiants.collectAsState()

    var etudiants by remember(dbEtudiants) {
        mutableStateOf(dbEtudiants.map {
            Etudiant(it.id, it.nom, it.prenom, false)
        })
    }
    var recherche by remember { mutableStateOf("") }
    var confirme by remember { mutableStateOf(false) }
    var showJustifDialog by remember { mutableStateOf<Pair<Etudiant, Boolean>?>(null) } // Etudiant and isLate flag

    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                selectedFileName = uri.lastPathSegment ?: "justificatif.pdf"
            }
        }
    )

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
                            "${etudiants.count { it.estPresent || it.estEnRetard }} présent(s) sur ${etudiants.size}",
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
                actions = {
                    IconButton(onClick = onScanClick) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner QR", tint = Color.White)
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
            if (!confirme) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = {
                            viewModel.saveAttendance(etudiants)
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
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(100.dp).background(VertPresent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Présence enregistrée !", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VertPresent)
                    Spacer(Modifier.height(32.dp))
                    OutlinedButton(onClick = onRetour) {
                        Text("Retour à l'accueil")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).background(GrisFond)
            ) {
                OutlinedTextField(
                    value = recherche,
                    onValueChange = { recherche = it },
                    placeholder = { Text("Rechercher mon nom...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
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
                            onTogglePresent = {
                                etudiants = etudiants.map { e ->
                                    if (e.id == etudiant.id) {
                                        val newPresent = !e.estPresent
                                        e.copy(estPresent = newPresent, estEnRetard = if (newPresent) false else e.estEnRetard)
                                    } else e
                                }
                            },
                            onToggleRetard = {
                                if (!etudiant.estEnRetard) {
                                    showJustifDialog = Pair(etudiant, true)
                                } else {
                                    etudiants = etudiants.map { e ->
                                        if (e.id == etudiant.id) e.copy(estEnRetard = false) else e
                                    }
                                }
                            },
                            onJustifClick = { showJustifDialog = Pair(etudiant, false) }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }

    if (showJustifDialog != null) {
        val (etudiant, isLate) = showJustifDialog!!
        AlertDialog(
            onDismissRequest = { 
                showJustifDialog = null
                selectedFileName = null
            },
            confirmButton = {
                TextButton(onClick = {
                    etudiants = etudiants.map { e ->
                        if (e.id == etudiant.id) {
                            if (isLate) e.copy(estEnRetard = true, estPresent = false, justificatif = selectedFileName ?: "justif_retard")
                            else e.copy(justificatif = selectedFileName ?: "justif_absence")
                        } else e
                    }
                    showJustifDialog = null
                    selectedFileName = null
                }) { Text("Envoyer") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showJustifDialog = null
                    selectedFileName = null
                }) { Text("Annuler") }
            },
            title = { Text(if (isLate) "Justifier le Retard" else "Justifier l'absence") },
            text = {
                Column {
                    Text("Un justificatif est obligatoire pour marquer un ${if (isLate) "retard" else "absence"}.")
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedFileName == null) "Choisir un fichier" else "Changer de fichier")
                    }
                    if (selectedFileName != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Fichier : $selectedFileName",
                            fontSize = 12.sp,
                            color = VertPresent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun EtudiantItem(etudiant: Etudiant, onTogglePresent: () -> Unit, onToggleRetard: () -> Unit, onJustifClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                etudiant.estPresent -> Color(0xFFE8F5E9)
                etudiant.estEnRetard -> Color(0xFFFFF3E0)
                else -> BlancCard
            }
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(
                    when {
                        etudiant.estPresent -> VertPresent
                        etudiant.estEnRetard -> OrangeRetard
                        else -> BleuClair
                    }, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${etudiant.prenom.first()}${etudiant.nom.first()}",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${etudiant.prenom} ${etudiant.nom}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    text = when {
                        etudiant.estPresent -> "✓ Présent(e)"
                        etudiant.estEnRetard -> "⚠ En retard (Justifié)"
                        etudiant.justificatif != null -> "Justifié"
                        else -> "Absent"
                    },
                    fontSize = 13.sp, color = when {
                        etudiant.estPresent -> VertPresent
                        etudiant.estEnRetard -> OrangeRetard
                        else -> Color.Gray
                    }
                )
            }

            // Bouton Retard
            if (!etudiant.estPresent) {
                IconButton(onClick = onToggleRetard) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Retard",
                        tint = if (etudiant.estEnRetard) OrangeRetard else Color.Gray
                    )
                }
            }

            // Bouton Justificatif (pour absence pure)
            if (!etudiant.estPresent && !etudiant.estEnRetard) {
                IconButton(onClick = onJustifClick) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = "Justifier",
                        tint = if (etudiant.justificatif != null) VertPresent else Color.Gray
                    )
                }
            }

            Checkbox(
                checked = etudiant.estPresent,
                onCheckedChange = { onTogglePresent() },
                colors = CheckboxDefaults.colors(checkedColor = VertPresent, uncheckedColor = Color.LightGray)
            )
        }
    }
}
