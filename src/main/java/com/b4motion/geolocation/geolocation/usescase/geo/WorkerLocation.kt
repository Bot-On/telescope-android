package com.b4motion.geolocation.geolocation.usescase.geo

import android.annotation.SuppressLint
import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.geolocation.core.GeoB4
import com.b4motion.geolocation.geolocation.core.Telescope
import com.b4motion.geolocation.geolocation.globals.extensions.log
import com.b4motion.geolocation.geolocation.globals.extensions.toRequestFeedGPS
import com.google.android.gms.location.*
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.doAsync
import java.util.concurrent.CountDownLatch


/**
 * Created by Javier Camarero on 8/11/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
class WorkerLocation(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        const val LOCATION_REFRESH_TIME = 5000L
        const val LOCATION_REFRESH_DISTANCE = 0.1f
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val disposable: CompositeDisposable = CompositeDisposable()
    private lateinit var locationCallback: LocationCallback


    //------------ WORKER METHODS ---------------------
    //region WORKER METHODS
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        log("doWork")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        //val lastLocation = fusedLocationClient.lastLocation.result
        defineLocationListener()

        var handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        var looper = handlerThread.looper
        fusedLocationClient.requestLocationUpdates(buildLocationRequest(), locationCallback, looper)

        log("location wait")
        log("location count ${Telescope.locationWait.await()}")
        Telescope.locationWait.await()
        log("location released")
        return Result.SUCCESS
    }

    override fun onStopped(cancelled: Boolean) {
        super.onStopped(cancelled)
        disposable.dispose()
    }
    //endregion

    //------------ UTILS ---------------------
    //region UTILS
    private fun defineLocationListener() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                log("positionreceived")
                if (p0 != null) {
                    val lastLocation = p0.lastLocation
                    val position = PositionDb(System.currentTimeMillis(), Repository.getMobileId(context),
                            lastLocation.latitude,
                            lastLocation.longitude,
                            lastLocation.altitude,
                            lastLocation.bearing.toDouble(),
                            lastLocation.speed.toDouble())

                    sendLocations(position)

                }
            }
        }
    }

    private fun sendLocations(position: PositionDb) {
        doAsync {
            GeoB4.getInstance().database.poistionDao().insertPosition(position)
            disposable.add(
                    Repository.sendGPSData(GeoB4.getInstance().database.poistionDao().getAllPositionsAsc().toRequestFeedGPS())
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
            GeoB4.getInstance().database.poistionDao().delete()
            //locationWait.countDown()
        }
    }
    //endregion


}