package com.simplyapp.control.testing

import android.util.Log

import timber.log.Timber

class TestTree : Timber.Tree() {
    override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
        if (priority == Log.ERROR) {
            if (t == null) {
                System.err.println(tag + ": " + message)
            } else {
                System.err.println(tag + ": " + t.message + ": " + message)
            }
        } else {
            if (t == null) {
                println(tag + ": " + message)
            } else {
                println(tag + ": " + t.message + ": " + message)
            }
        }
    }
}
