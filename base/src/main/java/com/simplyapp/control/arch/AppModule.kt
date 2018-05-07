package com.simplyapp.control.arch

import android.annotation.SuppressLint
import android.app.Application

@SuppressLint("StaticFieldLeak")
object AppModule {
    lateinit var application: Application
}
