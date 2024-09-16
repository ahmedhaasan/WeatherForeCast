package com.example.weatherforecast.model.reposiatory

import WeatherResponse
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.remote.RemoteDataSourceContract
import com.example.weatherforecast.model.remote.RemoteDataSourceImp

class ReposiatoryImp(
    private val remote: RemoteDataSourceImp,
    private val local: LocalDataSourceImp
) : ReposiatoryContract {
    override suspend fun getCurrentWeatherRemotely(
        lat: Double,
        lon: Double,
        unit: String
    ): WeatherResponse? {
        return remote.getCurrentWeather(lat, lon, unit)
    }

    override suspend fun getCurrentLocalWeather(): WeatherResponse {
        return local.getCurrentWeather()
    }

    override suspend fun insertCurrentLocalWeather(c_weather: WeatherResponse): Long {
        return local.insertCurrentWeather(c_weather)
    }

    override suspend fun deleteCurrentLocalWeather(c_weather: WeatherResponse): Int {
        return local.deleteCurrentWeather(c_weather)
    }
}