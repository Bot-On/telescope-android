package com.b4motion.geolocation.data.storage.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by jcamarero on 11/11/2017.
 */

class PreferenceHelper<T>(private val context: Context, private val name: String = "") {


    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_GEOB4, Context.MODE_PRIVATE)
    }

    fun setPreference(value: T) = putPreference(name, value)

    fun getPreference(default: T) = findPreference(name, default)

    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }

    fun removePreference(value: String) {
        prefs.edit().remove(value).apply()
    }

    private fun <T> findPreference(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException(
                    "This type can't be saved into Preferences")
        }
        res as T
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }

}
