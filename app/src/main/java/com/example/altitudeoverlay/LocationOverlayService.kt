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
    private var lastLocation: Location? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var isInDismissZone = false

    private var dismissZoneTopY = 0

    private val COLOR_NORMAL = 0x99000000.toInt()
    private val COLOR_DISMISS = 0xCCD32F2F.toInt()

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

        val savedSizeName = prefs.getString(KEY_SIZE, OverlaySize.PICCOLA.name)
        currentSize = try {
            OverlaySize.valueOf(savedSizeName ?: OverlaySize.PICCOLA.name)
        } catch (e: IllegalArgumentException) {
            OverlaySize.PICCOLA
        }

        val screenHeight = resources.displayMetrics.heightPixels
        dismissZoneTopY = (screenHeight * 0.88).toInt()

        setupOverlay()
        startLocationUpdates()
        createNotification()
    }

    private fun setupOverlay() {
        overlayView = TextView(this).apply {
            text = getString(R.string.overlay_placeholder_text)
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(COLOR_NORMAL)
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
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 150
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                cycleOverlaySize()
                return true
            }
        })

        overlayView.setOnTouchListener { _, event ->
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

                    if (abs(dx) > 8 || abs(dy) > 8) {
                        isDragging = true
                    }

                    if (isDragging) {
                        overlayParams.x = initialX + dx.toInt()
                        overlayParams.y = initialY + dy.toInt()
                        windowManager.updateViewLayout(overlayView, overlayParams)

                        val nowInDismissZone = overlayParams.y >= dismissZoneTopY
                        if (nowInDismissZone != isInDismissZone) {
                            isInDismissZone = nowInDismissZone
                            overlayView.setBackgroundColor(
                                if (isInDismissZone) COLOR_DISMISS else COLOR_NORMAL
                            )
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging && isInDismissZone) {
                        stopSelf()
                    }
                    isDragging = false
                    isInDismissZone = false
                    true
                }
                else -> false
            }
        }

        windowManager.addView(overlayView, overlayParams)
        isOverlayShowing = true
    }

    private fun applySizeToView() {
        overlayView.textSize = currentSize.textSizeSp
        val p = currentSize.paddingPx
        overlayView.setPadding(p, (p * 0.8).toInt(), p, (p * 0.8).toInt())
    }

    private fun cycleOverlaySize() {
        val values = OverlaySize.entries.toTypedArray()
        val nextIndex = (currentSize.ordinal + 1) % values.size
        currentSize = values[nextIndex]

        applySizeToView()
        windowManager.updateViewLayout(overlayView, overlayParams)
        prefs.edit().putString(KEY_SIZE, currentSize.name).apply()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                lastLocation = location
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
        val altitudeGrezza = location.altitude
        val undulation = GeoidCorrection.getUndulation(this)
        val altitudeCorretta = GeoidCorrection.toOrthometricAltitude(altitudeGrezza, undulation).toInt()

        val text = if (GeoidCorrection.getShowAccuracy(this)) {
            val accuracy = location.accuracy.toInt()
            "⛰ ${altitudeCorretta}m (±${accuracy}m)"
        } else {
            "⛰ ${altitudeCorretta}m"
        }
        overlayView.text = text
    }

    private fun createNotification() {
        val channelId = "altitude_overlay_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
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
            isOverlayShowing = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}