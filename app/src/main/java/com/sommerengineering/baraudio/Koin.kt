package com.sommerengineering.baraudio

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.android.billingclient.api.BillingClient
import com.sommerengineering.baraudio.login.BillingClientImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// koin modules
val appModule = module {
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext())}
    single<Repository> { Repository() }
    viewModel { MainViewModel(get(), get()) }
    single<BillingClientImpl> { param -> BillingClientImpl(param.get<Context>()) }
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)