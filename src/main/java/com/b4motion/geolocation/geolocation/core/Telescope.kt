package com.b4motion.geolocation.geolocation.core

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.geolocation.globals.extensions.getTelescopeInfo
import com.b4motion.geolocation.geolocation.usescase.geo.ServiceRequestLocation


/**
 * Created by Javier Camarero on 26/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
class Telescope {

    companion object {

        private var telescope: Telescope? = null
        private var isRunning: Boolean = false

        @JvmStatic
        fun getInstance(activity: AppCompatActivity, imei: String): Telescope {
            if (telescope == null) {
                if (checkPermissions(activity)) {
                    ConnectionManager.initRetrofitClient(activity.applicationContext.getTelescopeInfo())
                    GeoLocation(activity).init(imei)
                    isRunning = true
                    telescope = Telescope()
                }
            }
            return telescope ?: getInstance(activity, imei)
        }

        @JvmStatic
        fun restartTracking(activity: AppCompatActivity) {
            if (telescope != null) {
                activity.applicationContext.startService(Intent(activity, ServiceRequestLocation::class.java))
                isRunning = true
            } else
                throw Exception("you have to call Telescope.getInstance(activity, imei) first")
        }


        @JvmStatic
        fun stopTracking(activity: AppCompatActivity) {
            activity.applicationContext.stopService(Intent(activity, ServiceRequestLocation::class.java))
            isRunning = false
        }

        @JvmStatic
        fun isRunning() = isRunning


        private fun checkPermissions(activity: AppCompatActivity): Boolean {
            var permissions = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

            /*if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.CALL_PHONE)*/

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.READ_PHONE_STATE)

            var permissionsToRequest = Array(permissions.size) { "" }

            for (i in 0 until permissions.size) {
                permissionsToRequest[i] = permissions[i]
            }

            if (permissionsToRequest.isNotEmpty())
                ActivityCompat.requestPermissions(activity, permissionsToRequest, 0)

            return permissionsToRequest.isEmpty()
        }
    }

}