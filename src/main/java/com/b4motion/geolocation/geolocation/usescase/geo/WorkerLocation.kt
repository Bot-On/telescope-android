package com.b4motion.geolocation.geolocation.usescase.geo

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.data.storage.GeoDatabase
import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.geolocation.core.Telescope
import com.b4motion.geolocation.geolocation.globals.extensions.getTelescopeInfo
import com.b4motion.geolocation.geolocation.globals.extensions.log
import com.b4motion.geolocation.geolocation.globals.extensions.toRequestFeedGPS
import com.google.android.gms.location.*
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.doAsync


/**
 * Created by Javier Camarero on 8/11/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
class WorkerLocation(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        const val LOCATION_REFRESH_TIME = 5000L
        const val LOCATION_REFRESH_DISTANCE = 1f
        const val MINIMUN_POSITIONS_TO_SEND = 5
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val disposable: CompositeDisposable = CompositeDisposable()
    private lateinit var locationCallback: LocationCallback
    private var lastLocationSaved: Location? = null
    lateinit var database: GeoDatabase


    //------------ WORKER METHODS ---------------------
    //region WORKER METHODS
    override fun doWork(): Result {
        log("starting location work")
        if (!::database.isInitialized)
            database = Room.databaseBuilder(context, GeoDatabase::class.java, "b4_geo_database").fallbackToDestructiveMigration().build()

        ConnectionManager.initRetrofitClient(context.applicationContext.getTelescopeInfo())

        startToLocate()

        Telescope.locationWait.await()
        return Result.SUCCESS
    }

    override fun onStopped(cancelled: Boolean) {
        super.onStopped(cancelled)
        log("on stop work location")
        disposable.dispose()
        if (::fusedLocationClient.isInitialized)
            fusedLocationClient.removeLocationUpdates(locationCallback)

    }
    //endregion

    //------------ UTILS ---------------------
    //region UTILS
    @SuppressLint("MissingPermission")
    private fun startToLocate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        //val lastLocation = fusedLocationClient.lastLocation.result
        defineFusedLocationListener()
        fusedLocationClient.requestLocationUpdates(buildLocationRequest(), locationCallback, Looper.getMainLooper())
    }

    private fun defineFusedLocationListener() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                log("positionreceived ${p0?.lastLocation?.latitude} : ${p0?.lastLocation?.longitude} : ${p0?.lastLocation?.time}")
                if (p0 != null)
                    handleLocation(p0.lastLocation)
            }
        }
    }

    private fun handleLocation(location: Location) {
        val position = PositionDb(location.time,
                Repository.getMobileId(context),
                location.latitude,
                location.longitude,
                location.altitude,
                location.bearing.toDouble(),
                getSpeed(location))
        log("send position")
        lastLocationSaved = location
        sendLocations(position)
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

    private fun sendLocations(position: PositionDb) {
        doAsync {
            ConnectionManager.initRetrofitClient(context.applicationContext.getTelescopeInfo())

            database.poistionDao().insertPosition(position)
            if (database.poistionDao().getAllPositionsAsc().size > MINIMUN_POSITIONS_TO_SEND)
                disposable.add(
                        Repository.sendGPSData(database.poistionDao().getAllPositionsAsc().toRequestFeedGPS())
                                .subscribe({ deletePosition() }, { connectionError() })
                )
        }
    }

    private fun buildLocationRequest(): LocationRequest {
        var locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = LOCATION_REFRESH_TIME
        locationRequest.fastestInterval = LOCATION_REFRESH_TIME
        locationRequest.smallestDisplacement = LOCATION_REFRESH_DISTANCE
        log("location request")
        return locationRequest
    }

    private fun connectionError() {
        log("conexion error")
        //locationWait.countDown()
    }

    private fun deletePosition() {
        log("removing position")
        doAsync {
            database.poistionDao().delete()
            //locationWait.countDown()
        }
    }
    //endregion


}