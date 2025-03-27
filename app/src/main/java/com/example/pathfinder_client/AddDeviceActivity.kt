package com.example.pathfinder_client

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var wifiAutoCompleteTextView: AutoCompleteTextView
    private lateinit var wifiManager: WifiManager

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        wifiAutoCompleteTextView = findViewById(R.id.wifiAutoCompleteTextView)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Check WiFi scanning permissions for both fine and coarse location
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_WIFI_STATE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            scanWifiNetworks()
        }
    }

    private fun scanWifiNetworks() {
        // Ensure WiFi is enabled
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "WiFi is disabled. Please enable it.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check permissions again before scanning
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Scan for WiFi networks
        val scanResults = wifiManager.scanResults

        // Extract network names (SSIDs)
        val wifiNetworks = scanResults.map { it.SSID.trim('"') }.distinct()



        // Create an adapter for the AutoCompleteTextView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            wifiNetworks
        )

        // Set the adapter to the AutoCompleteTextView
        wifiAutoCompleteTextView.setAdapter(adapter)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                scanWifiNetworks()
            } else {
                Toast.makeText(
                    this,
                    "Permissions needed to scan WiFi networks",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}