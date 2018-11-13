package com.b4motion.geolocation.data.cloud

import com.b4motion.geolocation.domain.model.Device
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import com.b4motion.geolocation.domain.model.Telescope
import com.b4motion.geolocation.domain.model.TelescopeResponse
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
class ConnectionManager {

    companion object {

        private lateinit var telescopeRetrofit : Retrofit

        fun initRetrofitClient(telescope: Telescope){
            telescopeRetrofit = RetrofitClient.getClient(telescope)
        }

        fun sendGPSData(position: RequestFeedGPS) : Completable {
            return telescopeRetrofit
                    .create(Services::class.java)
                    .sendGPSData(position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun getDeviceId(mobile_id : String) : Single<TelescopeResponse<MutableList<Device>>> {
            return telescopeRetrofit
                    .create(Services::class.java)
                    .getDeviceId(mobile_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun createDevice(imei : String) : Single<TelescopeResponse<Device>> {
            return telescopeRetrofit
                    .create(Services::class.java)
                    .createDevice(imei)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}