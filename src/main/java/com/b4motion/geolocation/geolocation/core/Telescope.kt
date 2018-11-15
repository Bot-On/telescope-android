package com.b4motion.geolocation.geolocation.core

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.geolocation.globals.extensions.getTelescopeInfo
import com.b4motion.geolocation.geolocation.globals.extensions.log
import com.b4motion.geolocation.geolocation.usescase.geo.ServiceRequestLocation
import com.b4motion.geolocation.geolocation.usescase.geo.WorkerLocation
/*import com.facebook.stetho.Stetho*/
import java.util.concurrent.CountDownLatch


/**
 * Created by Javier Camarero on 26/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
class Telescope {

    companion object {

        private var telescope: Telescope? = null
        private var isRunning: Boolean = false
        var locationWait = CountDownLatch(1)


        @JvmStatic
        fun getInstance(activity: AppCompatActivity, imei: String): Telescope {
            if (telescope == null) {
                if (checkPermissions(activity)) {
                    Repository.setMobileId(activity, imei)
                    isRunning = true
                    telescope = Telescope()
                    restartTracking()
                }
            }
//            Stetho.initializeWithDefaults(activity)
            return telescope ?: getInstance(activity, imei)
        }

        @JvmStatic
        fun restartTracking() {
            if (telescope != null) {

                locationWait = CountDownLatch(1)
                WorkManager.getInstance().beginUniqueWork("worklocation",
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.Builder(WorkerLocation::class.java).build())
                        .enqueue()

                log("restarting tracking")
                isRunning = true
            } else
                throw Exception("you have to call Telescope.getInstance(activity, imei) first")
        }


        @JvmStatic
        fun stopTracking() {
            //activity.applicationContext.stopService(Intent(activity, ServiceRequestLocation::class.java))
            isRunning = false

            locationWait.countDown()
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