package com.sommerengineering.baraudio.messages

import com.sommerengineering.baraudio.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface RapidApiService {

    // todo move headers to interceptor
    @Headers(
        "x-rapidapi-key: ${BuildConfig.rapidApiKey}",
        "x-rapidapi-host: metaapi-mindfulness-quotes.p.rapidapi.com"
    )

    @GET("v1/mindfulness")
    suspend fun getQuote() : MindfulnessQuote
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun provideRetrofit(): RapidApiService {

        return Retrofit.Builder()
            .baseUrl("https://metaapi-mindfulness-quotes.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RapidApiService::class.java)
    }
}

