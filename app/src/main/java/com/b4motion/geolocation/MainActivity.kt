package com.b4motion.geolocation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.b4motion.geolocation.core.GeoB4
import com.b4motion.geolocation.globals.log

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (hasPermissions()) {
            try {
                GeoB4.getInstance().init(applicationContext)
            } catch (e: SecurityException) {
                log("que no tienes permisos!!!!")
            }
        }
    }

    private fun hasPermissions(): Boolean {
        var permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_PHONE_STATE)

        var permissionsToRequest = Array(permissions.size) { "" }

        for (i in 0 until permissions.size) {
            permissionsToRequest[i] = permissions[i]
        }

        if (permissionsToRequest.isNotEmpty())
            ActivityCompat.requestPermissions(this, permissionsToRequest, 0)

        return permissionsToRequest.isEmpty()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //If ask is commig from share fragment,then continue there
        if (grantResults.isNotEmpty() && grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            GeoB4.getInstance().init(applicationContext)
        }
    }
}
