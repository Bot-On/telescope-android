package com.b4motion.geolocation.geolocation.core

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.b4motion.geolocation.geolocation.globals.extensions.log

/**
 * Created by frodriguez on 7/30/2018.
 *
 */
class GeoLocation constructor(private val activity: AppCompatActivity){

    fun init(deviceId: String){
        checkPermissions()
        try {
            GeoB4.getInstance().init(activity, deviceId)
        } catch (e: SecurityException) {
            log("que no tienes permisos!!!!")
        }
    }

    private fun checkPermissions() {
        var permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CALL_PHONE)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_PHONE_STATE)

        var permissionsToRequest = Array(permissions.size) { "" }

        for (i in 0 until permissions.size) {
            permissionsToRequest[i] = permissions[i]
        }

        if (permissionsToRequest.isNotEmpty())
            ActivityCompat.requestPermissions(activity, permissionsToRequest, 0)
    }

}