package com.b4motion.data.storage

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.b4motion.domain.db.PositionDb

@Dao
interface PositionDao {
    @Query("select * from positions order by timestamp asc")
    fun getAllPositionsAsc(): MutableList<PositionDb>

    @Query("select * from positions order by timestamp desc")
    fun getAllPositionsDesc(): MutableList<PositionDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosition(position: PositionDb)
}