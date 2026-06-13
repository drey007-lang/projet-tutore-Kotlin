package com.example.tp_b2a.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun ScannerScreen(
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit
) {
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            if (result.contents != null) {
                onScanResult(result.contents)
            } else {
                onCancel()
            }
        }
    )

    LaunchedEffect(Unit) {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scannez le code QR de la séance")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
        Text("Ouverture du scanner...", modifier = Modifier.padding(top = 80.dp))
    }
}
