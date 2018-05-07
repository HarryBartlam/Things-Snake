package com.simplyapp.control

import android.app.Application
import com.simplyapp.control.testing.TestTree
import timber.log.Timber

class TestTemplateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(TestTree())
    }

}
