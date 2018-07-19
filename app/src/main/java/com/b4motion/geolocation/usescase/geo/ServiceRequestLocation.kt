package com.b4motion.geolocation.usescase.geo

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.telephony.TelephonyManager
import android.util.Log
import com.b4motion.data.Repository
import com.b4motion.data.storage.preferences.PREF_IMEI
import com.b4motion.data.storage.preferences.PreferenceHelper
import com.b4motion.domain.db.PositionDb
import com.b4motion.geolocation.R
import com.b4motion.geolocation.core.GeoB4
import com.b4motion.geolocation.globals.buildNotificationChanel
import com.b4motion.geolocation.globals.log
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.doAsync


class ServiceRequestLocation : Service() {
    companion object {
        const val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
        const val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
        const val SMALL_DISPLACEMENT: Float = 5f
    }

    private val disposable: CompositeDisposable = CompositeDisposable()

    private lateinit var locationRequest: LocationRequest
    private lateinit var position: PositionDb

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        saveImei()

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotificationChanel()
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }
        startForeground(12, buildNotification("Texto", channelId))

        startLocationUpdates()
        return super.onStartCommand(intent, flags, startId)
    }

    // Trigger new location updates at interval
    private fun startLocationUpdates() {
        buildLocationRequest()
        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        requestForLocations()
    }

    private fun onLocationChanged(location: Location) {
        position = PositionDb(System.currentTimeMillis(), Repository.getDeviceId(this), location.latitude, location.longitude)
        //position = PositionDb(System.currentTimeMillis(), "1a8d9325-0c21-4460-aa8e-e4f2424f9697", location.latitude, location.longitude)
        doAsync {
            GeoB4.getInstance().database.poistionDao().insertPosition(position)
            disposable.add(
                    Repository.sendGPSData(position)
                            .subscribe({deletePosition()}, {Log.d("Error", "Error")})
            )

            GeoB4.getInstance().database.poistionDao().getAllPositionsAsc().forEach { log("time ${it.timestamp}", "ASC") }
            GeoB4.getInstance().database.poistionDao().getAllPositionsDesc().forEach { log("time ${it.timestamp}", "DESC") }
        }
    }

    private fun deletePosition() {
        doAsync {
            GeoB4.getInstance().database.poistionDao().delete(position)
        }
    }

    private fun buildLocationRequest() {
        // Create the location request to start receiving updates
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL
        locationRequest.smallestDisplacement = SMALL_DISPLACEMENT
    }

    @SuppressLint("MissingPermission")
    private fun requestForLocations() {
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                // do work here
                if (locationResult != null)
                    onLocationChanged(locationResult.lastLocation)
            }
        }, Looper.myLooper())
    }

    @SuppressLint("MissingPermission")
    private fun saveImei() {
        var tm: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            PreferenceHelper<String>(this@ServiceRequestLocation, PREF_IMEI).setPreference(tm.deviceId)
            log(tm.deviceId)
        } else {
            PreferenceHelper<String>(this@ServiceRequestLocation, PREF_IMEI).setPreference(tm.imei)
            log(tm.imei)
        }
    }

    //------------ NOTIFICATION ---------------------
    //region NOTIFICATION
    private fun buildNotification(text: String, channelId: String): Notification {
        val notification = NotificationCompat.Builder(this, channelId)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)

        val notiBuild = notification.build()
        notiBuild.flags = Notification.FLAG_NO_CLEAR

        return notiBuild
    }
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
