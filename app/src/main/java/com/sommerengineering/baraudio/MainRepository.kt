package com.sommerengineering.baraudio

import com.sommerengineering.baraudio.hilt.RapidApi
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import javax.inject.Inject

class MainRepository @Inject constructor(
    val rapidApi: RapidApi,
    val tts: TextToSpeechImpl
    ) {

    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()



}