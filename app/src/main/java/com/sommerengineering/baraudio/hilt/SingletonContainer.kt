package com.sommerengineering.baraudio.hilt

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.sommerengineering.baraudio.localCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

val Context.dataStore by preferencesDataStore(localCache)

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideAppVisibility() : AppVisibility {
        return AppVisibility()
    }

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
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

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabaseImpl {
        return FirebaseDatabaseImpl()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): RapidApi {

        return Retrofit.Builder()
            .baseUrl("https://metaapi-mindfulness-quotes.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RapidApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context) : MessageDatabase {

        return Room.databaseBuilder(
            context,
            MessageDatabase::class.java,
            "messages.db"
        ).build()
    }

    @Provides
    fun provideMessageDao(db: MessageDatabase): MessageDao {
        return db.messageDao()
    }
}
