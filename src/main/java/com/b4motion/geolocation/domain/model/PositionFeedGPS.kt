package com.b4motion.geolocation.domain.model


/**
 * Created by Javier Camarero on 23/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
data class PositionFeedGPS(val timestamp: String,
                           val latitude: Double,
                           val longitude: Double,
                           val altitude: Double,
                           val bearing: Double,
                           val speed: Double)