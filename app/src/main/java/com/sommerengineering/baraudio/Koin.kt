package com.sommerengineering.baraudio

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// koin modules
val appModule = module {
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext())}
    viewModel { MainViewModel(get()) } // todo decouple this, why dependency here? need separation for previews
    single<BillingClientImpl> { param -> BillingClientImpl(param.get<Context>()) }
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)