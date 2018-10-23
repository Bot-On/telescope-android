package com.b4motion.geolocation.geolocation.globals

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

@RequiresApi(Build.VERSION_CODES.O)
fun Context.buildNotificationChanel(): String {
    val channelId = "my_service_channel"
    val chan = NotificationChannel(channelId, this.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
    val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(chan)
    return channelId
}

fun log(text: String, tag: String = "GeoB4") {
    if (BuildConfig.DEBUG)
        Log.d(tag, text)
}

fun MutableList<PositionDb>.toRequestFeedGPS() = RequestFeedGPS(this[0].deviceId, this.map {
    PositionFeedGPS(it.timestamp.toDouble(), it.latitude, it.longitude, it.altitude)
} as MutableList<PositionFeedGPS>)