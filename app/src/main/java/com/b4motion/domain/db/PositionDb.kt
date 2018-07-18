package com.b4motion.domain.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "positions")
data class PositionDb(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "timestamp") var timestamp: Long = 0L,
                      @ColumnInfo(name = "latitude") var latitude : Double = 0.0,
                      @ColumnInfo(name = "longitude") var longitude : Double = 0.0)