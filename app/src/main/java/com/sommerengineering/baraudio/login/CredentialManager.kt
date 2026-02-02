package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CredentialManagerModule {

    @Provides
    fun provideCredentialManager(
        @ApplicationContext context: Context): CredentialManager {

        return CredentialManager.create(context)
    }
}