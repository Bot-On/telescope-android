package com.b4motion.geolocation.data.cloud

import com.b4motion.geolocation.domain.db.PositionDb
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
class ConnectionManager {

    companion object {

        fun sendGPSData(position: PositionDb) : Completable {
            return RetrofitClient.getClient()
                    .create(Services::class.java)
                    .sendGPSData(position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}