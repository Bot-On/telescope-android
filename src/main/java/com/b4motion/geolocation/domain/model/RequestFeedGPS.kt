package com.b4motion.geolocation.domain.model


/**
 * Created by Javier Camarero on 23/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
data class RequestFeedGPS(var mobile_id: String, var batch: MutableList<PositionFeedGPS>,
                          var gps_position: PositionGPS)