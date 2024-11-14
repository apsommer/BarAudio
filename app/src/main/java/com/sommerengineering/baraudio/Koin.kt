package com.sommerengineering.baraudio

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

// koin modules
val appModule = module {
    single<Repository> { Repository() }
    viewModel { MainViewModel(get(), get()) }
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext())}
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)
