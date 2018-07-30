package com.b4motion.geolocation.geolocation.core

import android.content.Context
import com.b4motion.geolocation.geolocation.globals.log

/**
 * Created by frodriguez on 7/30/2018.
 *
 */
class GeoLocation constructor(private val applicationContext: Context){

    fun init(deviceId: String){
        try {
            GeoB4.getInstance().init(applicationContext, deviceId)
        } catch (e: SecurityException) {
            log("que no tienes permisos!!!!")
        }
    }


}