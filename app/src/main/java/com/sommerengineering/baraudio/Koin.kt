package com.sommerengineering.baraudio

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

// koin modules
val appModule = module {
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext())}
    viewModel { MainViewModel(get()) }

    // todo refactor as viewmodel dependency
    single<BillingClientImpl> { param -> BillingClientImpl(param.get<Context>()) }
    single<CredentialManager> { CredentialManager.create(androidContext()) }

    single<RapidApiService> { initRetrofit() }
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)
