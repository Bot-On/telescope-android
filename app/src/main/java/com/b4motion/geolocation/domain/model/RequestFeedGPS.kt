package com.b4motion.geolocation.domain.model


/**
 * Created by Javier Camarero on 23/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
data class RequestFeedGPS(var device_id : String, var batch : MutableList<PositionFeedGPS>)