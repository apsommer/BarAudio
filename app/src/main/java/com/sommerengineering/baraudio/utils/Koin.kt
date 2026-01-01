package com.sommerengineering.baraudio.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.preferences.preferencesDataStore
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.utils.TextToSpeechImpl
import com.sommerengineering.baraudio.localCache
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// koin modules
val appModule = module {
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext()) }
    viewModel { MainViewModel(get()) }

    // todo refactor as viewmodel dependency
    single<BillingClientImpl> { param -> BillingClientImpl(param.get<Context>()) }
    single<CredentialManager> { CredentialManager.create(androidContext()) }

    single<RapidApi> { initRetrofit() }
}

// preferences datastore
val Context.dataStore by preferencesDataStore(localCache)
