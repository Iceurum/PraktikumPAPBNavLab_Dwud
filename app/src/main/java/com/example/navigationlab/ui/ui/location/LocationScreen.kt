package com.example.navigationlab.ui.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationlab.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (coarse || fine) {
            viewModel.refreshLocation()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    val requestPermission = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    val getOrRequestLocation = {
        if (hasLocationPermission(context)) {
            viewModel.refreshLocation()
        } else {
            requestPermission()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Lokasi Saya",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (val state = uiState) {
                        LocationUiState.Idle -> {
                            Text("Lokasi belum diminta")
                        }

                        LocationUiState.Loading -> {
                            CircularProgressIndicator()
                            Text("Mengambil lokasi...")
                        }

                        is LocationUiState.Success -> {
                            AccessLabel(isPrecise = state.location.isPrecise)
                            Text(
                                text = "Lokasi ditemukan",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Latitude: ${state.location.latitude}")
                            Text("Longitude: ${state.location.longitude}")
                            Text(
                                "Akurasi: " + (state.location.accuracy
                                    ?.let { String.format(Locale.US, "%.1f m", it) }
                                    ?: "tidak diketahui")
                            )
                            Text(
                                text = "Diperbarui: ${formatTime(state.updatedAt)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        is LocationUiState.Error -> {
                            Text(
                                text = "Gagal mengambil lokasi",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(state.message, textAlign = TextAlign.Center)
                        }

                        LocationUiState.PermissionDenied -> {
                            Text(
                                text = "Izin lokasi ditolak",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Aplikasi membutuhkan izin lokasi untuk menampilkan posisi kamu.",
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = requestPermission) {
                                Text("Minta Izin Lagi")
                            }
                            TextButton(onClick = { openAppSettings(context) }) {
                                Text("Buka Pengaturan Aplikasi")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState != LocationUiState.PermissionDenied) {
                Button(
                    onClick = getOrRequestLocation,
                    enabled = uiState != LocationUiState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when (uiState) {
                            is LocationUiState.Success -> "Perbarui Lokasi"
                            is LocationUiState.Error -> "Coba Lagi"
                            else -> "Ambil Lokasi Saya"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Kembali", color = Color.White)
            }
        }
    }
}

@Composable
private fun AccessLabel(isPrecise: Boolean) {
    Text(
        text = if (isPrecise) "Precise" else "Approximate",
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isPrecise) Color(0xFF2E7D32) else Color(0xFFEF6C00))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

private fun formatTime(millis: Long): String =
    SimpleDateFormat("HH:mm:ss, dd MMM yyyy", Locale.forLanguageTag("id-ID"))
        .format(Date(millis))

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
