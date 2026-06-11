package com.example.tp_b2a.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Écran de connexion générique (Délégué & Enseignant) ──────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    titre: String,
    emoji: String,
    couleur: Color,
    onRetour: () -> Unit,
    /** Retourne true si le code est incorrect */
    onConnexion: (String) -> Boolean
) {
    var code   by remember { mutableStateOf("") }
    var erreur by remember { mutableStateOf(false) }

    BackHandler { onRetour() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(couleur, couleur.copy(alpha = 0.75f))))
    ) {
        // Bouton retour
        IconButton(
            onClick   = onRetour,
            modifier  = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
        }

        Column(
            modifier              = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn() + slideInVertically { -it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 64.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(titre, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        "Entrez votre code d'accès",
                        fontSize = 16.sp,
                        color    = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 36.dp)
                    )
                }
            }

            // Carte de connexion
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value           = code,
                        onValueChange   = { code = it; erreur = false },
                        label           = { Text("Code d'accès") },
                        leadingIcon     = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = couleur)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError         = erreur,
                        supportingText  = {
                            if (erreur) Text(
                                "Code incorrect. Réessayez.",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier        = Modifier.fillMaxWidth(),
                        shape           = RoundedCornerShape(12.dp),
                        singleLine      = true,
                        colors          = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = couleur,
                            focusedLabelColor  = couleur
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick  = { erreur = onConnexion(code) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = couleur),
                        enabled  = code.isNotBlank()
                    ) {
                        Text("Se connecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── Écran de confirmation générique ──────────────────────────────────────

@Composable
fun ConfirmationScreen(
    message: String,
    detail: String,
    couleur: Color,
    onRetour: () -> Unit
) {
    BackHandler { onRetour() }

    Box(
        modifier        = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            // Icône animée
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn() + slideInVertically { it }
            ) {
                Box(
                    modifier        = Modifier
                        .size(110.dp)
                        .background(couleur, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            Text(message, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = couleur)
            Spacer(Modifier.height(10.dp))
            Text(detail, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(40.dp))

            Button(
                onClick  = onRetour,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = couleur)
            ) {
                Text("Retour à l'accueil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Carte statistique réutilisable ───────────────────────────────────────

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    valeur: String,
    couleur: Color
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = couleur),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valeur, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label,  fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}
