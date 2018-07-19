package com.b4motion.data.cloud

import android.content.Context
import com.b4motion.domain.db.PositionDb
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