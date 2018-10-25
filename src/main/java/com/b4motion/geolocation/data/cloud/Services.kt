package com.b4motion.geolocation.data.cloud

import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
interface Services {

    companion object {

    }

    @POST(UrlConstants.DATA_FEEDS_GPS)
    fun sendGPSData(@Body position: RequestFeedGPS): Completable
}