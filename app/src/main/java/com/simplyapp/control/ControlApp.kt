package com.simplyapp.control

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.simplyapp.control.arch.AppModule

import timber.log.Timber

open class ControlApp : Application() {
    override fun onCreate() {
        AppModule.application = this
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidThreeTen.init(this)
    }

}
