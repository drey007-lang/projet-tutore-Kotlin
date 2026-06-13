package com.example.tp_b2a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tp_b2a.navigation.AppNavigation
import com.example.tp_b2a.ui.theme.TP_B2ATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TP_B2ATheme {
                AppNavigation()
            }
        }
    }
}
