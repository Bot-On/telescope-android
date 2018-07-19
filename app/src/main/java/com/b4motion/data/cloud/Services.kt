package com.b4motion.data.cloud

import com.b4motion.domain.db.PositionDb
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
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
    fun sendGPSData(@Body position: PositionDb): Completable
}