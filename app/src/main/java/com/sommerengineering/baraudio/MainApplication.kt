package com.sommerengineering.baraudio

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // disable analytics for debug builds
        if (BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
        }
    }
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)

// todo
//single<TextToSpeechImpl> { TextToSpeechImpl(androidContext()) }
//    viewModel { MainViewModel(get()) }

// todo refactor as viewmodel dependency
//single<BillingClientImpl> { param -> BillingClientImpl(param.get<Context>()) }
//single<CredentialManager> { CredentialManager.create(androidContext()) }
//    single<RapidApiService> { initRetrofit() }