package com.sommerengineering.baraudio

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.database.database
import com.sommerengineering.baraudio.hilt.AppVisibility
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject lateinit var appVisibility: AppVisibility

    override fun onCreate() {
        super.onCreate()


        // enable offline mode with local persistence
        Firebase
            .database(databaseUrl)
            .setPersistenceEnabled(true)

        // disable analytics for debug builds
        if (BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
        }
    }
}
