package com.sommerengineering.baraudio.utils

import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.messages.Quote
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
    suspend fun getQuote() : Quote
}

fun initRetrofit(): RapidApiService {

    return Retrofit.Builder()
        .baseUrl("https://metaapi-mindfulness-quotes.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RapidApiService::class.java)
}

