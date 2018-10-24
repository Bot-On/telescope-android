package com.b4motion.geolocation.data.cloud

import com.b4motion.geolocation.domain.db.PositionDb
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
class ConnectionManager {

    companion object {

        fun sendGPSData(position: RequestFeedGPS) : Completable {
            return RetrofitClient.getClient()
                    .create(Services::class.java)
                    .sendGPSData(position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun getDeviceId(imei : String) : Completable {
            return RetrofitClient.getClient()
                    .create(Services::class.java)
                    .getDeviceId(imei)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun createDevice(imei : String) : Completable {
            return RetrofitClient.getClient()
                    .create(Services::class.java)
                    .createDevice(imei)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}