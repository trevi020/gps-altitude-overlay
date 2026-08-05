package com.example.altitudeoverlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.abs

class LocationOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: TextView
    private lateinit var overlayParams: WindowManager.LayoutParams
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var gestureDetector: GestureDetector
    private lateinit var prefs: SharedPreferences
    private var isOverlayShowing = false

    // Stato per il trascinamento
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false

    // Dimensioni disponibili per l'overlay (ciclo: PICCOLA -> MEDIA -> GRANDE -> PICCOLA ...)
    private enum class OverlaySize(val textSizeSp: Float, val paddingPx: Int) {
        PICCOLA(16f, 24),
        MEDIA(24f, 32),
        GRANDE(34f, 40)
    }

    private var currentSize: OverlaySize = OverlaySize.PICCOLA

    companion object {
        private const val PREFS_NAME = "overlay_prefs"
        private const val KEY_SIZE = "overlay_size"
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Ripristina l'ultima dimensione scelta (default: PICCOLA)
        val savedSizeName = prefs.getString(KEY_SIZE, OverlaySize.PICCOLA.name)
        currentSize = try {
            OverlaySize.valueOf(savedSizeName ?: OverlaySize.PICCOLA.name)
        } catch (e: IllegalArgumentException) {
            OverlaySize.PICCOLA
        }

        setupOverlay()
        startLocationUpdates()
        createNotification()
    }

    private fun setupOverlay() {
        overlayView = TextView(this).apply {
            text = "Altitudine: --"
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0x99000000.toInt())
            gravity = Gravity.CENTER
        }
        applySizeToView()

        overlayParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSLUCENT
            // NOTA: niente FLAG_NOT_TOUCHABLE, altrimenti non si può trascinare/toccare
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 150
        }

        // GestureDetector per riconoscere il doppio tap senza interferire col drag
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                cycleOverlaySize()
                return true
            }
        })

        // Listener combinato: doppio tap per cambiare dimensione, drag per spostare
        overlayView.setOnTouchListener { _, event ->
            // Passa sempre l'evento al gesture detector per intercettare il doppio tap
            gestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = overlayParams.x
                    initialY = overlayParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY

                    // Considera "trascinamento" solo oltre una piccola soglia
                    if (abs(dx) > 8 || abs(dy) > 8) {
                        isDragging = true
                    }

                    if (isDragging) {
                        overlayParams.x = initialX + dx.toInt()
                        overlayParams.y = initialY + dy.toInt()
                        windowManager.updateViewLayout(overlayView, overlayParams)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    isDragging = false
                    true
                }
                else -> false
            }
        }

        windowManager.addView(overlayView, overlayParams)
        isOverlayShowing = true
    }

    /**
     * Applica la dimensione corrente (testo + padding) alla view dell'overlay.
     */
    private fun applySizeToView() {
        overlayView.textSize = currentSize.textSizeSp
        val p = currentSize.paddingPx
        overlayView.setPadding(p, (p * 0.8).toInt(), p, (p * 0.8).toInt())
    }

    /**
     * Passa alla dimensione successiva nel ciclo PICCOLA -> MEDIA -> GRANDE -> PICCOLA.
     * Poiché width/height dell'overlay sono WRAP_CONTENT, basta aggiornare la view
     * e poi ri-applicare il layout: Android ricalcola automaticamente le dimensioni.
     */
    private fun cycleOverlaySize() {
        val values = OverlaySize.entries.toTypedArray()
        val nextIndex = (currentSize.ordinal + 1) % values.size
        currentSize = values[nextIndex]

        applySizeToView()

        // Forza il ricalcolo del layout (WRAP_CONTENT) mantenendo la posizione x/y attuale
        windowManager.updateViewLayout(overlayView, overlayParams)

        // Salva la preferenza per la prossima apertura
        prefs.edit().putString(KEY_SIZE, currentSize.name).apply()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000 // Aggiornamento ogni 1 secondo
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                updateOverlay(location)
            }
        }

        try {
            @Suppress("MissingPermission")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun updateOverlay(location: Location) {
        val altitude = location.altitude.toInt()
        val accuracy = location.accuracy.toInt()
        val text = "⛰ ${altitude}m (±${accuracy}m)"
        overlayView.text = text
    }

    private fun createNotification() {
        val channelId = "altitude_overlay_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overlay Altitudine",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Overlay Altitudine Attivo")
            .setContentText("Monitoraggio posizione in corso... (doppio tap per cambiare dimensione)")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isOverlayShowing) {
            windowManager.removeView(overlayView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
