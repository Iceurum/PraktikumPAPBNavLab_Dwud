package com.example.navigationlab.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class DefaultLocationRepository(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<DeviceLocation> = runCatching {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        check(fineGranted || coarseGranted) { "Izin lokasi belum diberikan" }

        val priority = if (fineGranted) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val location = client.getCurrentLocation(priority, null).await()
            ?: if (!fineGranted) {
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            } else {
                null
            }
            ?: client.lastLocation.await()
            ?: error("Lokasi belum tersedia. Pastikan Location di perangkat aktif.")

        DeviceLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            isPrecise = fineGranted
        )
    }
}
