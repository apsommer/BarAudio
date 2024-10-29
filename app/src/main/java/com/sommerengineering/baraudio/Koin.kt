package com.sommerengineering.baraudio

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// koin modules
val appModule = module {
    single<FirebaseAuth> { Firebase.auth }
    single<Repository> { Repository() }
    viewModel { MainViewModel(get()) }
    single<TextToSpeechImpl> { TextToSpeechImpl(androidContext())}
}