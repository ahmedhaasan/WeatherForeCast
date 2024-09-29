package com.example.weatherforecast.model.reposiatory.fakedatasources

import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.remote.RemoteDataSourceContract
import kotlinx.coroutines.flow.Flow

class FakeRemoteDataSource : RemoteDataSourceContract {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<FiveDayResponse> {
        TODO("Not yet implemented")
    }
}