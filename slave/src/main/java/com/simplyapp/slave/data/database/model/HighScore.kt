package com.simplyapp.slave.data.database.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.simplyapp.slave.data.database.HIGH_SCORE_TABLE_NAME

@Entity(tableName = HIGH_SCORE_TABLE_NAME)
data class HighScore(
        @PrimaryKey(autoGenerate = false)
        var id: Int = 0,
        val score: Int,
        val initials: String,
        val date: Long)