package com.example.weatherforecast.model.connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "https://api.openweathermap.org/"

    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Pass the OkHttpClient instance here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Lazily initialized service instance
    val service: WeatherApiService by lazy {
        retrofitInstance.create(WeatherApiService::class.java)
    }
}
