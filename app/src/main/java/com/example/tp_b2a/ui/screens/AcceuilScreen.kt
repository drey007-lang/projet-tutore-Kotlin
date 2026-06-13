package com.example.tp_b2a.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_b2a.ui.theme.*

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
            .background(
                Brush.verticalGradient(
                    colors = listOf(RosePrimaire, RoseClair)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titre
            Text(
                text = "PresenZ 🚀",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )
            Text(
                text = "Choisissez votre profil",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 8.dp, bottom = 64.dp)
            )

            // Boutons circulaires alignés horizontalement
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                CircularRoleButton(
                    emoji = "🎓",
                    label = "Étudiant",
                    couleur = RosePrimaire,
                    onClick = onEtudiantClick
                )

                CircularRoleButton(
                    emoji = "📌",
                    label = "Délégué",
                    couleur = RoseClair,
                    onClick = onDelegueClick
                )

                CircularRoleButton(
                    emoji = "👨‍🏫",
                    label = "Enseignant",
                    couleur = Color(0xFFC2185B), // Deep Pink
                    onClick = onEnseignantClick
                )
            }
        }
    }
}

@Composable
fun CircularRoleButton(
    emoji: String,
    label: String,
    couleur: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(couleur)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
