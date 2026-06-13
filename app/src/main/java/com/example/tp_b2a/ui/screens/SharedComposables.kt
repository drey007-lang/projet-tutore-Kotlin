package com.example.tp_b2a.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.tp_b2a.ui.theme.*
import kotlinx.coroutines.launch

// ─── Écran de connexion générique (réutilisé par Délégué et Enseignant) ────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    titre: String,
    emoji: String,
    couleur: Color,
    onRetour: () -> Unit,
    onConnexion: suspend (String, String) -> Boolean
) {
    var nom by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var erreur by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(couleur, couleur.copy(alpha = 0.7f))))
    ) {
        // Bouton retour en haut à gauche
        IconButton(
            onClick = onRetour,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emoji et titre
            Text(emoji, fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(titre, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                "Identifiez-vous pour continuer",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 4.dp, bottom = 40.dp)
            )

            // Card de connexion
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Champ Nom
                    OutlinedTextField(
                        value = nom,
                        onValueChange = {
                            nom = it
                            erreur = false
                        },
                        label = { Text("Nom ou Prénom") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = couleur)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = erreur,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = couleur,
                            focusedLabelColor = couleur
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // Champ Code/Mot de passe
                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            code = it
                            erreur = false
                        },
                        label = { Text("Code d'accès") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = couleur)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = erreur,
                        supportingText = {
                            if (erreur) Text("Identifiants incorrects.", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = couleur,
                            focusedLabelColor = couleur
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                val success = onConnexion(nom.trim(), code.trim().uppercase())
                                erreur = !success
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = couleur),
                        enabled = nom.isNotBlank() && code.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Se connecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ─── Écran de confirmation générique ───────────────────────────────────────

@Composable
fun ConfirmationScreen(
    message: String,
    detail: String,
    couleur: Color,
    onRetour: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFond),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(couleur, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(60.dp))
            }
            Spacer(Modifier.height(28.dp))
            Text(message, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = couleur)
            Spacer(Modifier.height(10.dp))
            Text(detail, fontSize = 15.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = onRetour,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = couleur)
            ) {
                Text("Retour à l'accueil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
