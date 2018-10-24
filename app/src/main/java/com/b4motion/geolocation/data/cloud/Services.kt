package com.b4motion.geolocation.data.cloud

import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import io.reactivex.Completable
import retrofit2.http.*

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
interface Services {

    @GET(UrlConstants.DEVICES)
    fun getDeviceId(@Query("imei") deviceId: String): Completable

    @FormUrlEncoded
    @POST(UrlConstants.DEVICES)
    fun createDevice(@Field("imei") imei: String, @Field("type") type: String = "phone"): Completable

    @POST(UrlConstants.DATA_FEEDS_GPS)
    fun sendGPSData(@Body position: RequestFeedGPS): Completable

}