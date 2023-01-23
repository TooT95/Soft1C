package com.example.soft1c

import android.app.Application
import timber.log.Timber

class ApplicationSoft:Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}