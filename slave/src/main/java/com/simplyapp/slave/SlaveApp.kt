package com.simplyapp.slave

import android.app.Application
import com.simplyapp.control.arch.AppModule
import timber.log.Timber

open class SlaveApp : Application() {
    override fun onCreate() {
        AppModule.application = this
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
