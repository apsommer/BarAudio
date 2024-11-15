package com.sommerengineering.baraudio

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
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
    }
}

// todo configure proguard for Alert pojo
//  https://firebase.google.com/docs/database/android/start#proguard

// todo complete launch checklist prior to production
//  https://firebase.google.com/support/guides/launch-checklist

// todo implement App Check via Google Play Integrity API, setup flow through firebase console
//  https://firebase.google.com/docs/app-check/android/play-integrity-provider?hl=en&authuser=0&_gl=1*4ksu49*_ga*NTE3MjAzMTkwLjE3Mjg1NTI5MDE.*_ga_CW55HF8NVT*MTcyOTM2MTg3NS4xOC4xLjE3MjkzNjQzODIuMC4wLjA.

// todo display app-wide banner for 'no internet connection', for example sign-in currently fails silently
