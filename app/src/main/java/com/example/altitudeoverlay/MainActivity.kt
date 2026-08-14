package com.example.altitudeoverlay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var seekBarCorrection: SeekBar
    private lateinit var textCorrectionValue: TextView
    private lateinit var textRawAltitude: TextView
    private lateinit var textCorrectedAltitude: TextView
    private lateinit var switchShowAccuracy: Switch
    private lateinit var btnPresetItalia: Button
    private lateinit var btnPresetUk: Button
    private lateinit var btnPresetCustom: Button

    private var isOverlayRunning = false

    private val PERMISSION_REQUEST_CODE = 100
    private val OVERLAY_PERMISSION_REQUEST_CODE = 101

    // Offset per mappare lo SeekBar (0..200) sul range reale (-100..+100)
    private val SEEKBAR_OFFSET = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.btn_start)
        seekBarCorrection = findViewById(R.id.seekbar_correction)
        textCorrectionValue = findViewById(R.id.text_correction_value)
        textRawAltitude = findViewById(R.id.text_raw_altitude)
        textCorrectedAltitude = findViewById(R.id.text_corrected_altitude)
        switchShowAccuracy = findViewById(R.id.switch_show_accuracy)
        btnPresetItalia = findViewById(R.id.btn_preset_italia)
        btnPresetUk = findViewById(R.id.btn_preset_uk)
        btnPresetCustom = findViewById(R.id.btn_preset_custom)

        // Precompila lo stato con i valori salvati
        val currentValue = GeoidCorrection.getUndulation(this)
        setSeekBarFromValue(currentValue)
        updateCorrectionLabel(currentValue)
        updatePresetSelection(currentValue)

        switchShowAccuracy.isChecked = GeoidCorrection.getShowAccuracy(this)

        seekBarCorrection.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val value = (progress - SEEKBAR_OFFSET).toDouble()
                updateCorrectionLabel(value)
                updatePresetSelection(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val value = (seekBar?.progress ?: SEEKBAR_OFFSET) - SEEKBAR_OFFSET
                GeoidCorrection.setUndulation(this@MainActivity, value.toDouble())
                refreshAltitudeCard()
            }
        })

        btnPresetItalia.setOnClickListener {
            applyPreset(GeoidCorrection.DEFAULT_ITALIA)
        }

        btnPresetUk.setOnClickListener {
            applyPreset(55.0)
        }

        btnPresetCustom.setOnClickListener {
            // "Custom" non applica un valore fisso: invita l'utente a usare lo slider
            Toast.makeText(this, "Usa lo slider per impostare un valore personalizzato", Toast.LENGTH_SHORT).show()
        }

        switchShowAccuracy.setOnCheckedChangeListener { _, isChecked ->
            GeoidCorrection.setShowAccuracy(this, isChecked)
        }

        startButton.setOnClickListener {
            if (isOverlayRunning) {
                stopOverlay()
            } else {
                checkPermissionsAndStart()
            }
        }

        refreshAltitudeCard()
    }

    override fun onResume() {
        super.onResume()
        refreshAltitudeCard()
    }

    /**
     * Applica un valore preset: aggiorna slider, label, preferenze e ricalcola la card.
     */
    private fun applyPreset(value: Double) {
        setSeekBarFromValue(value)
        updateCorrectionLabel(value)
        updatePresetSelection(value)
        GeoidCorrection.setUndulation(this, value)
        refreshAltitudeCard()
    }

    private fun setSeekBarFromValue(value: Double) {
        val progress = (value.toInt() + SEEKBAR_OFFSET).coerceIn(0, seekBarCorrection.max)
        seekBarCorrection.progress = progress
    }

    private fun updateCorrectionLabel(value: Double) {
        textCorrectionValue.text = String.format("%.1fm", value)
    }

    /**
     * Evidenzia il preset corrispondente al valore corrente (se combacia con uno dei due fissi),
     * altrimenti evidenzia "Custom".
     */
    private fun updatePresetSelection(value: Double) {
        val isItalia = value == GeoidCorrection.DEFAULT_ITALIA
        val isUk = value == 55.0

        btnPresetItalia.setBackgroundResource(
            if (isItalia) R.drawable.bg_preset_selected else R.drawable.bg_preset_unselected
        )
        btnPresetItalia.setTextColor(
            ContextCompat.getColor(this, if (isItalia) R.color.trail_orange else R.color.trail_text_secondary)
        )

        btnPresetUk.setBackgroundResource(
            if (isUk) R.drawable.bg_preset_selected else R.drawable.bg_preset_unselected
        )
        btnPresetUk.setTextColor(
            ContextCompat.getColor(this, if (isUk) R.color.trail_orange else R.color.trail_text_secondary)
        )

        val isCustom = !isItalia && !isUk
        btnPresetCustom.setBackgroundResource(
            if (isCustom) R.drawable.bg_preset_selected else R.drawable.bg_preset_unselected
        )
        btnPresetCustom.setTextColor(
            ContextCompat.getColor(this, if (isCustom) R.color.trail_orange else R.color.trail_text_secondary)
        )
    }

    /**
     * Legge l'ultima posizione nota (se disponibile e permesso concesso) e aggiorna
     * la card grezza/corretta in cima alla schermata.
     */
    private fun refreshAltitudeCard() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        try {
            val client = LocationServices.getFusedLocationProviderClient(this)
            @Suppress("MissingPermission")
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener
                val raw = location.altitude
                val undulation = GeoidCorrection.getUndulation(this)
                val corrected = GeoidCorrection.toOrthometricAltitude(raw, undulation)

                textRawAltitude.text = "${raw.toInt()}m"
                textCorrectedAltitude.text = "${corrected.toInt()}m"
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
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
                refreshAltitudeCard()
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

        isOverlayRunning = true
        startButton.text = "Ferma Overlay"
        Toast.makeText(this, "Overlay avviato", Toast.LENGTH_SHORT).show()
    }

    private fun stopOverlay() {
        val intent = Intent(this, LocationOverlayService::class.java)
        stopService(intent)

        isOverlayRunning = false
        startButton.text = "Avvia Overlay"
        Toast.makeText(this, "Overlay fermato", Toast.LENGTH_SHORT).show()
    }
}