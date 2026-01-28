package com.sommerengineering.baraudio

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.crashlytics
import com.sommerengineering.baraudio.utils.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize koin
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        // disable analytics for debug builds
        if (BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
        }
    }
}
