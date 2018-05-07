package com.simplyapp.data.arch

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.simplyapp.control.arch.AppModule

object PreferenceModule {
    private val PREF_TOKEN = "token"

    private val sharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(AppModule.application)
    }

    private val rxSharedPrefs by lazy {
        RxSharedPreferences.create(sharedPrefs)
    }

    val tokenPref by lazy {
        rxSharedPrefs.getString(PREF_TOKEN, "")
    }
}
