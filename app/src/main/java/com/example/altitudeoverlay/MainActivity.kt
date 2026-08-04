package com.example.altitudeoverlay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.btn_start)
        stopButton = findViewById(R.id.btn_stop)

        startButton.setOnClickListener {
            if (hasRequiredPermissions()) {
                startOverlay()
            } else {
                requestPermissions()
            }
        }

        stopButton.setOnClickListener {
            stopOverlay()
        }

        // Disabilita il pulsante stop inizialmente
        stopButton.isEnabled = false
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SYSTEM_ALERT_WINDOW
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SYSTEM_ALERT_WINDOW
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startOverlay()
            } else {
                Toast.makeText(
                    this,
                    "Permessi necessari per il funzionamento dell'app",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startOverlay() {
        val intent = Intent(this, LocationOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        startButton.isEnabled = false
        stopButton.isEnabled = true
        Toast.makeText(this, "Overlay avviato", Toast.LENGTH_SHORT).show()
    }

    private fun stopOverlay() {
        val intent = Intent(this, LocationOverlayService::class.java)
        stopService(intent)

        startButton.isEnabled = true
        stopButton.isEnabled = false
        Toast.makeText(this, "Overlay fermato", Toast.LENGTH_SHORT).show()
    }
}
