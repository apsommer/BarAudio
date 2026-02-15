package com.sommerengineering.baraudio.messages

import com.sommerengineering.baraudio.BuildConfig
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