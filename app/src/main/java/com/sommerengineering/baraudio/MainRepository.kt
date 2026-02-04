package com.sommerengineering.baraudio

import com.sommerengineering.baraudio.hilt.RapidApi
import javax.inject.Inject

class MainRepository @Inject constructor(
    val rapidApi: RapidApi,

    ) {

    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()



}