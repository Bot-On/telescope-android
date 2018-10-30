package com.b4motion.geolocation.geolocation.core

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

import com.b4motion.geolocation.data.Repository
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.domain.model.Device
import com.b4motion.geolocation.domain.model.TelescopeResponse
import com.b4motion.geolocation.geolocation.globals.extensions.getTelescopeInfo

import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException

import com.b4motion.geolocation.geolocation.globals.extensions.log
import io.reactivex.Single
import retrofit2.Response


/**
 * Created by frodriguez on 7/30/2018.
 *
 */
class GeoLocation constructor(val activity: AppCompatActivity) {
    private val disposable: CompositeDisposable = CompositeDisposable()
    private var imei: String = ""


    fun init(deviceId: String) {
        try {
            imei = deviceId
            getDeviceId()
        } catch (e: SecurityException) {
            log("que no tienes permisos!!!!")
        }
    }

    //------------ CLOUD ---------------------
    //region CLOUD
    private fun getDeviceId() {
        var type = ""
        disposable.add(Repository.getDeviceId(imei).subscribe({
            if (it.data.size > 0)
                startTracking(it.data.firstOrNull { device -> device.attributes.imei == imei }?.apply { type = attributes.type }?.id, type)
            else
                createDeviceId()
        }, { handleError(it) }))
    }

    private fun createDeviceId() {
        disposable.add(Repository.createDevice(imei).subscribe({ startTracking(it.data.id, it.data.attributes.type) }, { }))
    }

    private fun startTracking(mobileId: String?, type: String) {
        Repository.setMobileId(activity, mobileId ?: "")
        Repository.setDeviceType(activity, type)
        GeoB4.getInstance().init(activity)
    }

    private fun handleError(cause: Throwable?) {
        when (cause) {
            is HttpException -> {
                if (cause.code() == 404) {
                    createDeviceId()
                }
            }
        }
    }
    //endregion
}