package com.example.tp_b2a.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Couleurs du thème ──────────────────────────────────────────────────────
val BleuPrimaire   = Color(0xFF1A3A6B)
val BleuClair      = Color(0xFF2E6DB4)
val VertPresent    = Color(0xFF2E7D32)
val RougeAbsent    = Color(0xFFC62828)
val OrangeRetard   = Color(0xFFE65100)
val GrisFond       = Color(0xFFF4F6FA)
val BlancCard      = Color(0xFFFFFFFF)

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
                    colors = listOf(BleuPrimaire, BleuClair)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titre
            Text(
                text = "📋",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Gestion des\nPrésences",
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
                modifier = Modifier.padding(top = 8.dp, bottom = 56.dp)
            )

            // Bouton Étudiant
            RoleButton(
                emoji = "🎓",
                label = "Étudiant",
                description = "Marquer ma présence",
                couleur = Color(0xFF1565C0),
                onClick = onEtudiantClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Délégué
            RoleButton(
                emoji = "📌",
                label = "Délégué",
                description = "Gérer la liste de classe",
                couleur = Color(0xFF6A1B9A),
                onClick = onDelegueClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Enseignant
            RoleButton(
                emoji = "👨‍🏫",
                label = "Enseignant",
                description = "Valider et consulter",
                couleur = Color(0xFF00695C),
                onClick = onEnseignantClick
            )
        }
    }
}

@Composable
fun RoleButton(
    emoji: String,
    label: String,
    description: String,
    couleur: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = couleur),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "›", fontSize = 28.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}
