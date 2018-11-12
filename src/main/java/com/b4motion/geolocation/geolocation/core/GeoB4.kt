package com.b4motion.geolocation.geolocation.core

import android.Manifest
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import androidx.work.*
import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.data.storage.GeoDatabase
import com.b4motion.geolocation.geolocation.globals.extensions.log
import com.b4motion.geolocation.geolocation.usescase.geo.ServiceRequestLocation
import com.b4motion.geolocation.geolocation.usescase.geo.WorkerLocation
import java.util.concurrent.TimeUnit

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
        log("init geo b4")
        database = Room.databaseBuilder(activity, GeoDatabase::class.java, "b4_geo_database").fallbackToDestructiveMigration().build()
        if (hasPermissions(activity)) {
            Repository.setMobileId(activity, mobileId)
            //activity.startService(Intent(activity, ServiceRequestLocation::class.java))

            val workerLocation =
                    OneTimeWorkRequest.Builder(WorkerLocation::class.java)

            //workerLocation.addTag("workerLocation")

            WorkManager.getInstance().enqueue(workerLocation.build())
           /* WorkManager.getInstance().beginUniqueWork(
                    "workerLocation",
                    ExistingWorkPolicy.REPLACE,
                    workerLocation.build()
            ).enqueue()*/
            log("init worker en init de GeoB4")

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