package com.simplyapp.slave.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.simplyapp.slave.data.database.model.HighScore

@Database(entities = [(HighScore::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun highScoreDao(): HighScoreDao
}