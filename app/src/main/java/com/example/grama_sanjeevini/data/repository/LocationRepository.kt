package com.example.grama_sanjeevini.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import kotlin.math.*

data class UserLocation(
    val lat: Double,
    val lng: Double,
    val areaName: String
)

class LocationRepository(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    /** Emits a single UserLocation and then closes the flow. */
    fun getLocationFlow(): Flow<UserLocation?> = callbackFlow {
        if (!hasPermission()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val area = resolveAreaName(loc.latitude, loc.longitude)
                trySend(UserLocation(loc.latitude, loc.longitude, area))
                fusedClient.removeLocationUpdates(this)
                close()
            }
        }

        // Try last-known first for instant display
        fusedClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                val area = resolveAreaName(loc.latitude, loc.longitude)
                trySend(UserLocation(loc.latitude, loc.longitude, area))
            }
        }

        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }

    private fun resolveAreaName(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                addr.subLocality?.takeIf { it.isNotBlank() }
                    ?: addr.locality?.takeIf { it.isNotBlank() }
                    ?: addr.subAdminArea?.takeIf { it.isNotBlank() }
                    ?: addr.adminArea
                    ?: "Your Location"
            } else "Your Location"
        } catch (_: Exception) {
            "Your Location"
        }
    }

    companion object {
        /** Haversine formula — returns distance in km between two lat/lng points */
        fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val r = 6371.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLng / 2).pow(2)
            return r * 2 * atan2(sqrt(a), sqrt(1 - a))
        }
    }
}
