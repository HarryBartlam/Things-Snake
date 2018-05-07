package com.simplyapp.slave.arch

import android.arch.persistence.room.Room
import com.simplyapp.control.arch.AppModule
import com.simplyapp.slave.data.database.AppDatabase
import com.simplyapp.slave.data.database.HIGH_SCORE_TABLE_NAME
import com.simplyapp.slave.data.database.HighScoreDao


object DatabaseModule {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(AppModule.application, AppDatabase::class.java, HIGH_SCORE_TABLE_NAME)
                .build()
    }
    internal val highScoreDao: HighScoreDao by lazy {
        database.highScoreDao()
    }

}
