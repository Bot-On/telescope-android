package com.b4motion.geolocation.geolocation.globals.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import com.b4motion.geolocation.domain.model.Telescope
import com.google.gson.Gson


/**
 * Created by Javier Camarero on 26/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Context.buildNotificationChanel(): String {
    val channelId = "my_service_channel"
    val chan = NotificationChannel(channelId, this.getString(com.b4motion.geolocation.geolocation.R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
    val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(chan)
    return channelId
}

fun Context.getTelescopeInfo() : Telescope {
    val jsonTelescope = this.assets.open("telescope.json").bufferedReader().use {
        it.readText()
    }
    return Gson().fromJson(jsonTelescope, Telescope::class.java)
}