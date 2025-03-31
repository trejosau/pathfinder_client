package com.example.pathfinder_client.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import androidx.core.content.ContextCompat

class WifiConnectionManager(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    suspend fun connectToWifi(ssid: String, password: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectToWifiApi29(ssid, password)
        } else {
            false
        }
    }

    private suspend fun connectToWifiApi29(ssid: String, password: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val wifiSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiSpecifier)
                .build()

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    continuation.resume(true)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    continuation.resume(false)
                }
            }

            connectivityManager.requestNetwork(networkRequest, networkCallback)

            continuation.invokeOnCancellation {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }
    }

    suspend fun loadAvailableWifiNetworks(): List<String> = withContext(Dispatchers.IO) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Verificar si el permiso de ubicación está concedido
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Permission to access Wi-Fi scan results is not granted.")
        }
        // Realizar el escaneo
        val scanResults = wifiManager.scanResults
print(scanResults)
        // Mapeamos los resultados del escaneo a una lista de SSIDs usando getSSID()
        scanResults.map { it.SSID }
            .filter { it.isNotEmpty() }
            .distinct()
    }
}
