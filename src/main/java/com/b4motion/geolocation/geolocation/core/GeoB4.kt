package com.b4motion.geolocation.geolocation.core

import android.Manifest
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.data.storage.GeoDatabase
import com.b4motion.geolocation.geolocation.globals.extensions.getTelescopeInfo
import com.b4motion.geolocation.geolocation.usescase.geo.ServiceRequestLocation

class GeoB4 {
    lateinit var database: GeoDatabase

    companion object Factory {
        private var geoB4: GeoB4? = null

        /**
         * Starts geolocalization
         */
        fun getInstance(): GeoB4 {
            return if (geoB4 != null)
                geoB4!!
            else {
                geoB4 = GeoB4()
                return geoB4!!
            }
        }
    }

    /**
     * Init Geolocation Service
     * Requires ACCESS_FINE_LOCATION permission and READ_PHONE_STATE
     *
     * @throws SecurityException if ACCESS_FINE_LOCATION and READ_PHONE_STATE permissions aren't given
     */
    fun init(activity: AppCompatActivity, mobileId: String) {
        database = Room.databaseBuilder(activity, GeoDatabase::class.java, "b4_geo_database").fallbackToDestructiveMigration().build()
        if (hasPermissions(activity)) {
            Repository.setMobileId(activity, mobileId)
            activity.startService(Intent(activity, ServiceRequestLocation::class.java))
        } else
            throw(SecurityException())
    }


    private fun hasPermissions(context: Context): Boolean {
        var permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_PHONE_STATE)

        var permissionsToRequest = Array(permissions.size) { "" }

        for (i in 0 until permissions.size) {
            permissionsToRequest[i] = permissions[i]
        }

        return permissionsToRequest.isEmpty()
    }

}