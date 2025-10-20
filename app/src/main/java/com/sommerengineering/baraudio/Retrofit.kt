package com.sommerengineering.baraudio

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface RapidApiService {

    @Headers(
        "x-rapidapi-key: ${BuildConfig.rapidApiKey}",
        "x-rapidapi-host: metaapi-mindfulness-quotes.p.rapidapi.com"
    )

    @GET("v1/mindfulness")
    suspend fun getQuote() : MindfulnessQuote
}

fun initRetrofit(): RapidApiService {

    return Retrofit.Builder()
        .baseUrl("https://metaapi-mindfulness-quotes.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RapidApiService::class.java)
}

data class MindfulnessQuote(
    val quote: String,
    val category: String
)

data class MindfulnessQuoteState(
    var isLoading : Boolean = false,
    var isError: Boolean = false,
    var quote: String? = null
)