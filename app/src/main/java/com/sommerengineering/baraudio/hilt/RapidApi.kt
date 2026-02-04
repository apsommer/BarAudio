package com.sommerengineering.baraudio.hilt

import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.messages.MindfulnessQuote
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface RapidApi {

    // todo move headers to interceptor
    @Headers(
        "x-rapidapi-key: ${BuildConfig.rapidApiKey}",
        "x-rapidapi-host: metaapi-mindfulness-quotes.p.rapidapi.com"
    )

    @GET("v1/mindfulness")
    suspend fun getMindfulnessQuote() : MindfulnessQuote
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun provideRetrofit(): RapidApi {

        return Retrofit.Builder()
            .baseUrl("https://metaapi-mindfulness-quotes.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RapidApi::class.java)
    }
}

