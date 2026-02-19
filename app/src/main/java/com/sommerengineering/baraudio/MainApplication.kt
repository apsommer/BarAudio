package com.sommerengineering.baraudio

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.database.database
import com.sommerengineering.baraudio.uitls.databaseUrl
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject lateinit var processState: ProcessState

    override fun onCreate() {
        super.onCreate()

        // track app process state
        processState.isAlive = true

        // enable database offline mode with local persistence
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
