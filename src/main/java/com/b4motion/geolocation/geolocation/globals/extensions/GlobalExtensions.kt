package com.b4motion.geolocation.geolocation.globals.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.domain.model.PositionFeedGPS
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import com.b4motion.geolocation.geolocation.BuildConfig
import com.b4motion.geolocation.geolocation.R


/**
 * Created by Javier Camarero on 26/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */

fun log(text: String, tag: String = "GeoB4") {
    if (BuildConfig.DEBUG)
        Log.d(tag, text)
}

