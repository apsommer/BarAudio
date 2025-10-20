package com.sommerengineering.baraudio

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface RapidApiService {
//    @Headers(
//        "x-rapidapi-key: ${BuildConfig.rapidApiKey}",
//        "x-rapidapi-host: metaapi-mindfulness-quotes.p.rapidapi.com"
//    )
//    @GET("v1/mindfulness")
}

fun initRetrofit() {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://metaapi-mindfulness-quotes.p.rapidapi.com/")
//        .addConverterFactory(GsonConver)
        .build()

    val service = retrofit.create(RapidApiService::class.java)
}

data class MindfulnessQuote(
    val quote: String,
    val category: String)