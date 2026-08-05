package com.example.altitudeoverlay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var saveCorrectionButton: Button
    private lateinit var geoidCorrectionField: EditText
    private val PERMISSION_REQUEST_CODE = 100
    private val OVERLAY_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.btn_start)
        stopButton = findViewById(R.id.btn_stop)
        saveCorrectionButton = findViewById(R.id.btn_save_correction)
        geoidCorrectionField = findViewById(R.id.edit_geoid_correction)

        val currentValue = GeoidCorrection.getUndulation(this)
        geoidCorrectionField.setText(currentValue.toString())

        startButton.setOnClickListener {
            checkPermissionsAndStart()
        }

        stopButton.setOnClickListener {
            stopOverlay()
        }

        saveCorrectionButton.setOnClickListener {
            saveGeoidCorrection()
        }

        stopButton.isEnabled = false
    }

    private fun saveGeoidCorrection() {
        val inputText = geoidCorrectionField.text.toString().trim()
        val value = inputText.toDoubleOrNull()

        if (value == null) {
            Toast.makeText(
                this,
                "Inserisci un numero valido (es. 47 oppure -60)",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        GeoidCorrection.setUndulation(this, value)
        Toast.makeText(
            this,
            "Correzione salvata: ${value}m. Riavvia l'overlay per applicarla.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun checkPermissionsAndStart() {
        if (!hasOverlayPermission()) {
            requestOverlayPermission()
            return
        }
        if (!hasLocationPermission()) {
            requestLocationPermissions()
            return
        }
        startOverlay()
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        Toast.makeText(
            this,
            "Attiva il permesso overlay per questa app, poi torna indietro",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun requestLocationPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission()) {
                checkPermissionsAndStart()
            } else {
                Toast.makeText(this, "Permesso overlay non concesso", Toast.LENGTH_SHORT).show()
            }
        }
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
                Toast.makeText(this, "Permesso localizzazione necessario", Toast.LENGTH_SHORT).show()
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
