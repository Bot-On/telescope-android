package com.b4motion.geolocation.geolocation.globals.extensions

import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.domain.model.PositionFeedGPS
import com.b4motion.geolocation.domain.model.RequestFeedGPS


/**
 * Created by Javier Camarero on 26/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
fun MutableList<PositionDb>.toRequestFeedGPS() = RequestFeedGPS(this[this.size - 1].deviceId, this.map {
    com.b4motion.geolocation.domain.model.PositionFeedGPS(it.timestamp.toZuluFormat(), it.latitude, it.longitude, it.altitude, it.bearing, it.speed)
} as MutableList<PositionFeedGPS>)