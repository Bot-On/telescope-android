package com.b4motion.geolocation.data

import android.content.Context
import com.b4motion.geolocation.data.storage.preferences.PREF_MOBILE_ID
import com.b4motion.geolocation.data.storage.preferences.PreferenceHelper
import com.b4motion.geolocation.data.cloud.ConnectionManager
import com.b4motion.geolocation.data.storage.preferences.PREF_DEVICE_TYPE
import com.b4motion.geolocation.domain.model.Device
import com.b4motion.geolocation.domain.model.RequestFeedGPS
import com.b4motion.geolocation.domain.model.TelescopeResponse
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
class Repository {

    companion object {

        //STORAGE
        fun getMobileId(context: Context): String {
            return PreferenceHelper<String>(context, PREF_MOBILE_ID).getPreference("")
        }

        fun setMobileId(context: Context, moileId: String) {
            PreferenceHelper<String>(context, PREF_MOBILE_ID).setPreference(moileId)
        }

        fun setDeviceType(context: Context, type: String) {
            PreferenceHelper<String>(context, PREF_DEVICE_TYPE).setPreference(type)
        }

        //CLOUD
        fun getDeviceId(mobile_id: String): Single<TelescopeResponse<MutableList<Device>>> {
            return ConnectionManager.getDeviceId(mobile_id)
        }

        fun createDevice(mobile_id: String): Completable {
            return ConnectionManager.createDevice(mobile_id)
        }

        fun sendGPSData(position: RequestFeedGPS): Completable {
            return ConnectionManager.sendGPSData(position)
        }
    }
}