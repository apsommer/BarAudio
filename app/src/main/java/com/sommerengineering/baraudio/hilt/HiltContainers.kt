package com.sommerengineering.baraudio.hilt

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideTextToSpeech(
        @ApplicationContext context: Context): TextToSpeechImpl {
        return TextToSpeechImpl(context)
    }

    @Provides
    @Singleton
    fun provideBillingClient(
        @ApplicationContext context: Context): BillingClientImpl {
        return BillingClientImpl(context)
    }
}