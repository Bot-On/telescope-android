package com.b4motion.geolocation.data.cloud

import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.domain.model.Device
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import com.b4motion.geolocation.domain.model.TelescopeResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
interface Services {

    @GET(UrlConstants.DEVICES)
    fun getDeviceId(@Query("mobile_id") mobile_id: String): Single<TelescopeResponse<MutableList<Device>>>

    @FormUrlEncoded
    @POST(UrlConstants.DEVICES)
    fun createDevice(@Field("mobile_id") imei: String, @Field("type") type: String = "phone"): Completable

    @POST(UrlConstants.DATA_FEEDS_GPS)
    fun sendGPSData(@Body position: RequestFeedGPS): Completable

}