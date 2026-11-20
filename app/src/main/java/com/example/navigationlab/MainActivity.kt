package com.example.navigationlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Color(0xFF2C4A6E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E2F2),
    onPrimaryContainer = Color(0xFF0F2238),
    secondary = Color(0xFFFFB300),
    onSecondary = Color(0xFF1A1A1A),
    surface = Color(0xFFF1F4F8),
    onSurface = Color(0xFF1B2430),
    surfaceVariant = Color(0xFFE3E9F0),
    onSurfaceVariant = Color(0xFF3A4654)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColors) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationLabApp()
                }
            }
        }
    }
}