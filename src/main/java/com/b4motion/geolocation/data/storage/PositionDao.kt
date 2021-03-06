package com.b4motion.geolocation.data.storage

import android.arch.persistence.room.*
import com.b4motion.geolocation.domain.db.PositionDb

@Dao
interface PositionDao {
    @Query("select * from positions order by timestamp asc")
    fun getAllPositionsAsc(): MutableList<PositionDb>

    @Query("select * from positions order by timestamp desc")
    fun getAllPositionsDesc(): MutableList<PositionDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosition(position: PositionDb)

    @Delete
    fun delete(position: PositionDb)

    @Query("DELETE FROM positions")
    fun delete()

}