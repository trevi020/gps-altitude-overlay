package com.example.altitudeoverlay

import android.content.Context
import android.content.SharedPreferences

object GeoidCorrection {

    private const val PREFS_NAME = "overlay_prefs"
    private const val KEY_GEOID_UNDULATION = "geoid_undulation_n"

    const val DEFAULT_ITALIA = 47.0

    fun getUndulation(context: Context): Double {
        val prefs = getPrefs(context)
        return prefs.getFloat(KEY_GEOID_UNDULATION, 0f).toDouble()
    }

    fun setUndulation(context: Context, value: Double) {
        getPrefs(context).edit().putFloat(KEY_GEOID_UNDULATION, value.toFloat()).apply()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun toOrthometricAltitude(altitudeEllissoidica: Double, undulation: Double): Double {
        return altitudeEllissoidica - undulation
    }
}
