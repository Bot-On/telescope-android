package com.b4motion.domain.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "positions")
data class PositionDb(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "timestamp") var timestamp: Long = 0L,
                      @Ignore @SerializedName("device_id")var deviceId: String = "",
                      @ColumnInfo(name = "latitude") var latitude : Double = 0.0,
                      @ColumnInfo(name = "longitude") var longitude : Double = 0.0)