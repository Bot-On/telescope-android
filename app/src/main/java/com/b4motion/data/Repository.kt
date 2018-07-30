package com.b4motion.data

import android.content.Context
import com.b4motion.data.cloud.ConnectionManager
import com.b4motion.data.storage.preferences.PREF_DEVICE_ID
import com.b4motion.data.storage.preferences.PreferenceHelper
import com.b4motion.domain.db.PositionDb
import io.reactivex.Completable

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
class Repository {

    companion object {

        //STORAGE
        fun getDeviceId(context: Context) : String {
            return PreferenceHelper<String>(context, PREF_DEVICE_ID).getPreference("")
        }

        //CLOUD
        fun sendGPSData(position: PositionDb) : Completable {
            return ConnectionManager.sendGPSData(position)
        }
    }
}