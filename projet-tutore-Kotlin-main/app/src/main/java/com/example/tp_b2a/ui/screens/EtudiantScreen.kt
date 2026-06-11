package com.example.tp_b2a.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.data.model.Etudiant
import com.example.tp_b2a.ui.theme.BleuClair
import com.example.tp_b2a.ui.theme.BleuPrimaire
import com.example.tp_b2a.ui.theme.GrisFond
import com.example.tp_b2a.ui.theme.VertPresent
import com.example.tp_b2a.viewmodel.PresenceViewModel

// ─── Étape de l'écran étudiant ─────────────────────────────────────────────
private enum class EtapeEtudiant { SELECTION, CONFIRMATION, SUCCES }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtudiantScreen(
    presenceViewModel: PresenceViewModel,
    onRetour: () -> Unit
) {
    var etape by remember { mutableStateOf(EtapeEtudiant.SELECTION) }
    var etudiantSelectionne by remember { mutableStateOf<Etudiant?>(null) }
    var recherche by remember { mutableStateOf("") }

    BackHandler {
        when (etape) {
            EtapeEtudiant.SELECTION    -> onRetour()
            EtapeEtudiant.CONFIRMATION -> etape = EtapeEtudiant.SELECTION
            EtapeEtudiant.SUCCES       -> onRetour()
        }
    }

    AnimatedContent(
        targetState = etape,
        transitionSpec = {
            (slideInHorizontally { it } + fadeIn()) togetherWith
            (slideOutHorizontally { -it } + fadeOut())
        },
        label = "etudiant_steps"
    ) { currentEtape ->
        when (currentEtape) {
            // ── Étape 1 : Sélection du nom ────────────────────────────────
            EtapeEtudiant.SELECTION -> {
                val etudiantsFiltres = presenceViewModel.etudiants.filter {
                    it.nom.contains(recherche, ignoreCase = true) ||
                    it.prenom.contains(recherche, ignoreCase = true)
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text("Qui êtes-vous ?", fontWeight = FontWeight.Bold)
                                    Text("Sélectionnez votre nom", fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f))
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = onRetour) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Retour", tint = Color.White)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor     = BleuPrimaire,
                                titleContentColor  = Color.White,
                                navigationIconContentColor = Color.White
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
                        // Barre de recherche
                        OutlinedTextField(
                            value         = recherche,
                            onValueChange = { recherche = it },
                            placeholder   = { Text("Rechercher mon nom...") },
                            leadingIcon   = { Icon(Icons.Default.Search, null) },
                            modifier      = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape         = RoundedCornerShape(12.dp),
                            singleLine    = true,
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = BleuClair,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor   = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        if (etudiantsFiltres.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Aucun étudiant trouvé", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(etudiantsFiltres, key = { it.id }) { etudiant ->
                                    val dejaPresent = presenceViewModel.estPresent(etudiant.id)
                                    SelectionEtudiantItem(
                                        etudiant    = etudiant,
                                        dejaPresent = dejaPresent,
                                        onClick     = {
                                            if (!dejaPresent) {
                                                etudiantSelectionne = etudiant
                                                etape = EtapeEtudiant.CONFIRMATION
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

            // ── Étape 2 : Confirmation ────────────────────────────────────
            EtapeEtudiant.CONFIRMATION -> {
                val etudiant = etudiantSelectionne ?: return@AnimatedContent
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrisFond),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier            = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar grand format
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(BleuClair, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = etudiant.initiales,
                                color      = Color.White,
                                fontSize   = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Vous êtes :", fontSize = 16.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            etudiant.nomComplet,
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color      = BleuPrimaire,
                            textAlign  = TextAlign.Center
                        )
                        Spacer(Modifier.height(40.dp))

                        Button(
                            onClick  = {
                                presenceViewModel.marquerPresent(etudiant.id)
                                etape = EtapeEtudiant.SUCCES
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = VertPresent)
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Confirmer ma présence", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick  = { etape = EtapeEtudiant.SELECTION },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape    = RoundedCornerShape(14.dp)
                        ) {
                            Text("Ce n'est pas moi", fontSize = 15.sp)
                        }
                    }
                }
            }

            // ── Étape 3 : Succès ──────────────────────────────────────────
            EtapeEtudiant.SUCCES -> {
                ConfirmationScreen(
                    message  = "Présence enregistrée !",
                    detail   = "Votre présence a bien été prise en compte, ${etudiantSelectionne?.prenom}.",
                    couleur  = VertPresent,
                    onRetour = onRetour
                )
            }
        }
    }
}

// ─── Item de sélection étudiant ───────────────────────────────────────────

@Composable
fun SelectionEtudiantItem(
    etudiant: Etudiant,
    dejaPresent: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (dejaPresent) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        enabled   = !dejaPresent
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (dejaPresent) VertPresent else BleuClair,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (dejaPresent) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(etudiant.initiales, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    etudiant.nomComplet,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = if (dejaPresent) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (dejaPresent) "✓ Présence déjà enregistrée" else "Appuyer pour sélectionner",
                    fontSize = 13.sp,
                    color    = if (dejaPresent) VertPresent else Color.Gray
                )
            }
            if (!dejaPresent) {
                Text("›", fontSize = 24.sp, color = Color.LightGray)
            }
        }
    }
}
