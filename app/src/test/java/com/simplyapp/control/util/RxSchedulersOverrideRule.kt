package com.simplyapp.control.util

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * This rule registers SchedulerHooks for RxJava and RxAndroid to ensure that subscriptions always
 * subscribeOn and observeOn Schedulers.immediate(). Warning, this rule will reset RxAndroidPlugins
 * and RxJavaPlugins before and after each test so if the application code uses RxJava plugins this
 * may affect the behaviour of the testing method.
 */

class RxSchedulersOverrideRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxAndroidPlugins.reset()
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> Schedulers.trampoline() }

                RxJavaPlugins.reset()
                RxJavaPlugins.setIoSchedulerHandler { scheduler -> Schedulers.trampoline() }
                RxJavaPlugins.setComputationSchedulerHandler { scheduler -> Schedulers.trampoline() }

                base.evaluate()

                RxAndroidPlugins.reset()
                RxJavaPlugins.reset()
            }
        }
    }
}
