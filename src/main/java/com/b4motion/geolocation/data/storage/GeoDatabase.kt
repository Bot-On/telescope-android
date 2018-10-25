package com.b4motion.geolocation.data.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.b4motion.geolocation.domain.db.PositionDb

@Database(entities = arrayOf(PositionDb::class), version = 2, exportSchema = false)
abstract class GeoDatabase : RoomDatabase() {

    abstract fun poistionDao(): PositionDao
}