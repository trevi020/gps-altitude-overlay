package com.example.altitudeoverlay

import android.Manifest
import android.animation.ValueAnimator
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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

    // --- Stato animazione onda ---
    // La fase va da 0 a 1: rappresenta quanto la view è traslata rispetto a metà
    // della sua larghezza. Avanza a ogni frame di una quantità proporzionale al
    // tempo trascorso, moltiplicata per waveSpeedFactor.
    private var waveTicker: ValueAnimator? = null
    private var waveSpeedAnimator: ValueAnimator? = null
    private var wavePhase = 0f
    private var waveSpeedFactor = 1f  // 1 = velocità piena, 0 = ferma
    private var lastFrameTimeNs = 0L

    private val WAVE_CYCLE_MS = 5000f       // durata di un ciclo completo a velocità piena
    private val WAVE_SPEED_RAMP_MS = 2000L  // tempo per fermarsi / ripartire gradualmente

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
            Toast.makeText(
                this,
                "Usa lo slider per impostare un valore personalizzato",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchShowAccuracy.setOnCheckedChangeListener { _, isChecked ->
            GeoidCorrection.setShowAccuracy(this, isChecked)
        }

        startButton.setOnClickListener {
            if (isOverlayServiceRunning()) {
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
        startWaveAnimation()
        syncOverlayButtonState()
    }

    override fun onPause() {
        super.onPause()
        // Ferma tutto in background per non consumare batteria
        waveTicker?.cancel()
        waveTicker = null
        waveSpeedAnimator?.cancel()
        waveSpeedAnimator = null
    }

    /**
     * Verifica se il servizio overlay è effettivamente in esecuzione, interrogando
     * il sistema invece di affidarsi a uno stato locale. Necessario perché il
     * servizio può fermarsi da solo (es. trascinando l'overlay nel cestino) senza
     * che l'Activity ne venga informata.
     */
    private fun isOverlayServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == LocationOverlayService::class.java.name }
    }

    /**
     * Allinea il testo del pulsante allo stato reale del servizio.
     */
    private fun syncOverlayButtonState() {
        startButton.text = if (isOverlayServiceRunning()) "Ferma Overlay" else "Avvia Overlay"
    }

    /**
     * Avvia lo scorrimento infinito dell'onda decorativa.
     *
     * L'immagine è larga il doppio dello schermo e contiene due ripetizioni identiche
     * del pattern: traslando esattamente di metà larghezza il punto di ripartenza
     * coincide e il loop risulta invisibile.
     *
     * Invece di un ObjectAnimator a durata fissa (che non permetterebbe di variare
     * la velocità in corsa), si usa un "ticker" che a ogni frame avanza la fase in
     * base al tempo realmente trascorso, moltiplicato per waveSpeedFactor.
     */
    private fun startWaveAnimation() {
        val waveImage = findViewById<ImageView>(R.id.wave_image) ?: return
        val waveContainer = findViewById<FrameLayout>(R.id.wave_container)

        waveImage.post {
            val screenWidth = resources.displayMetrics.widthPixels

            // L'immagine deve essere larga il doppio dello schermo: una metà copre
            // sempre l'area visibile mentre l'altra "entra" progressivamente da destra
            waveImage.layoutParams.width = screenWidth * 2
            waveImage.requestLayout()

            waveTicker?.cancel()
            lastFrameTimeNs = System.nanoTime()

            waveTicker = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1000
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                addUpdateListener {
                    val now = System.nanoTime()
                    val deltaMs = (now - lastFrameTimeNs) / 1_000_000f
                    lastFrameTimeNs = now

                    // Avanza la fase in proporzione al tempo trascorso e alla velocità corrente
                    wavePhase = (wavePhase + (deltaMs / WAVE_CYCLE_MS) * waveSpeedFactor) % 1f
                    waveImage.translationX = -wavePhase * screenWidth
                }
                start()
            }

            // Easter egg: tenendo premuto sull'onda, questa rallenta fino a fermarsi;
            // al rilascio riprende gradualmente la velocità originale.
            waveContainer?.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        animateWaveSpeedTo(0f)
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.performClick()
                        animateWaveSpeedTo(1f)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    /**
     * Porta gradualmente il fattore di velocità dell'onda al valore desiderato
     * (0 = ferma, 1 = velocità piena), con una rampa dolce.
     */
    private fun animateWaveSpeedTo(target: Float) {
        waveSpeedAnimator?.cancel()
        waveSpeedAnimator = ValueAnimator.ofFloat(waveSpeedFactor, target).apply {
            duration = WAVE_SPEED_RAMP_MS
            interpolator = LinearInterpolator()
            addUpdateListener { waveSpeedFactor = it.animatedValue as Float }
            start()
        }
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
     * Evidenzia il preset corrispondente al valore corrente; se non combacia con
     * nessuno dei due fissi, evidenzia "Custom".
     */
    private fun updatePresetSelection(value: Double) {
        val isItalia = value == GeoidCorrection.DEFAULT_ITALIA
        val isUk = value == 55.0
        val isCustom = !isItalia && !isUk

        applyPresetStyle(btnPresetItalia, isItalia)
        applyPresetStyle(btnPresetUk, isUk)
        applyPresetStyle(btnPresetCustom, isCustom)
    }

    private fun applyPresetStyle(button: Button, selected: Boolean) {
        button.setBackgroundResource(
            if (selected) R.drawable.bg_preset_selected else R.drawable.bg_preset_unselected
        )
        button.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected) R.color.trail_orange else R.color.trail_text_secondary
            )
        )
    }

    /**
     * Legge l'ultima posizione nota (se disponibile) e aggiorna la card
     * con altitudine grezza e corretta.
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

        startButton.text = "Ferma Overlay"
        Toast.makeText(this, "Overlay avviato", Toast.LENGTH_SHORT).show()
    }

    private fun stopOverlay() {
        val intent = Intent(this, LocationOverlayService::class.java)
        stopService(intent)

        startButton.text = "Avvia Overlay"
        Toast.makeText(this, "Overlay fermato", Toast.LENGTH_SHORT).show()
    }
}