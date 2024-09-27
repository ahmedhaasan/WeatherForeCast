package com.example.weatherforecast.model.reposiatory.fakedatasources

import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.remote.RemoteDataSourceContract

class FakeRemoteDataSource : RemoteDataSourceContract {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): WeatherResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): FiveDayResponse? {
        TODO("Not yet implemented")
    }
}