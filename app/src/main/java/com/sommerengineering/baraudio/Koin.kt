package com.sommerengineering.baraudio

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// koin modules
val appModule = module {
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext())}
    viewModel { MainViewModel(get()) }
    single<BillingClientImpl> { param -> BillingClientImpl(param.get<Context>()) } // todo refactor as viewmodel dependency
    single<CredentialManager> { CredentialManager.create(androidContext()) } // todo refactor as viewmodel dependency
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)