package com.criticalay.neer

import android.app.Application
import timber.log.Timber
import timber.log.Timber.Forest.plant


class NeerApp:Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }
}