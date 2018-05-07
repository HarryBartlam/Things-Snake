package com.simplyapp.slave.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.simplyapp.slave.data.database.model.HighScore
import io.reactivex.Single

const val HIGH_SCORE_TABLE_NAME = "high_score"

@Dao
interface HighScoreDao {

    @Insert(onConflict = REPLACE)
    fun insertHighScore(plate: List<HighScore>): LongArray

    @Query("SELECT * FROM $HIGH_SCORE_TABLE_NAME")
    fun getAllHighScores(): Single<List<HighScore>>

}

