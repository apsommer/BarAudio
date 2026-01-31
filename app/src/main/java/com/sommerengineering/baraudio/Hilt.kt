package com.sommerengineering.baraudio

import com.sommerengineering.baraudio.utils.RapidApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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