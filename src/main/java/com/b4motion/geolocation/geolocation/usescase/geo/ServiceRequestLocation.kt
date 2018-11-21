package com.b4motion.geolocation.geolocation.usescase.geo

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.arch.persistence.room.Room
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.NotificationCompat
import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.data.storage.GeoDatabase
import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.geolocation.R
import com.b4motion.geolocation.geolocation.globals.extensions.*
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
    private var lastLocationSaved: Location? = null
    private lateinit var position: PositionDb
    lateinit var database: GeoDatabase

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!::database.isInitialized)
            database = Room.databaseBuilder(this, GeoDatabase::class.java, "b4_geo_database").fallbackToDestructiveMigration().build()
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
        val position = PositionDb(location.time,
                Repository.getMobileId(this),
                location.latitude,
                location.longitude,
                location.altitude,
                location.bearing.toDouble(),
                getSpeed(location))
        log("send position")
        lastLocationSaved = location
        sendLocations(position)


        doAsync {
            /*GeoB4.getInstance().database.poistionDao().insertPosition(position)
            disposable.add(
                    Repository.sendGPSData(GeoB4.getInstance().database.poistionDao().getAllPositionsAsc().toRequestFeedGPS())
                            .subscribe({ deletePosition() }, { log("Error", "GeoLocation") })
            )*/
        }
    }

    private fun deletePosition() {
        /*doAsync {
            GeoB4.getInstance().database.poistionDao().delete()
        }*/
    }

    private fun getSpeed(currentLocation: Location): Double {
        return if (currentLocation.speed > 0)
            currentLocation.speed.toDouble()
        else {
            var speed = 0.0
            if (lastLocationSaved != null)
                speed = lastLocationSaved!!.distanceTo(currentLocation) /
                        ((currentLocation.time - lastLocationSaved!!.time).toDouble() / 1000)
            log("speed: $speed")
            speed
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

    private fun sendLocations(position: PositionDb) {
        doAsync {
            ConnectionManager.initRetrofitClient(applicationContext.getTelescopeInfo())

            database.poistionDao().insertPosition(position)
            if (database.poistionDao().getAllPositionsAsc().size > WorkerLocation.MINIMUN_POSITIONS_TO_SEND)
                disposable.add(
                        Repository.sendGPSData(database.poistionDao().getAllPositionsAsc().toRequestFeedGPS())
                                .subscribe({ deletePosition() }, { connectionError() })
                )
        }
    }

    private fun connectionError() {
        log("conexion error")
        //locationWait.countDown()
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

    //------------ NOTIFICATION ---------------------
    //region NOTIFICATION
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
