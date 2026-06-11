package com.example.tp_b2a.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.ui.theme.BleuClair
import com.example.tp_b2a.ui.theme.BleuPrimaire
import com.example.tp_b2a.ui.theme.CouleurDelegue
import com.example.tp_b2a.ui.theme.CouleurEnseignant
import com.example.tp_b2a.ui.theme.CouleurEtudiant

// ─── Écran d'accueil ────────────────────────────────────────────────────────

@Composable
fun AccueilScreen(
    onEtudiantClick: () -> Unit,
    onDelegueClick: () -> Unit,
    onEnseignantClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BleuPrimaire, BleuClair)))
    ) {
        Column(
            modifier              = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center
        ) {
            // Logo
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 72.sp, modifier = Modifier.padding(bottom = 16.dp))
                    Text(
                        "Gestion des\nPrésences",
                        fontSize    = 34.sp,
                        fontWeight  = FontWeight.ExtraBold,
                        color       = Color.White,
                        textAlign   = TextAlign.Center,
                        lineHeight  = 40.sp
                    )
                    Text(
                        "Choisissez votre profil",
                        fontSize = 16.sp,
                        color    = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 8.dp, bottom = 56.dp)
                    )
                }
            }

            // Boutons de rôle
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    RoleButton(
                        emoji       = "🎓",
                        label       = "Étudiant",
                        description = "Marquer ma présence",
                        couleur     = CouleurEtudiant,
                        onClick     = onEtudiantClick
                    )
                    RoleButton(
                        emoji       = "📌",
                        label       = "Délégué",
                        description = "Gérer la liste de classe",
                        couleur     = CouleurDelegue,
                        onClick     = onDelegueClick
                    )
                    RoleButton(
                        emoji       = "👨‍🏫",
                        label       = "Enseignant",
                        description = "Valider et consulter",
                        couleur     = CouleurEnseignant,
                        onClick     = onEnseignantClick
                    )
                }
            }
        }
    }
}

// ─── Bouton de rôle ──────────────────────────────────────────────────────────

@Composable
fun RoleButton(
    emoji: String,
    label: String,
    description: String,
    couleur: Color,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth().height(80.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = couleur),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier           = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment  = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label,       fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(description, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("›", fontSize = 28.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}
