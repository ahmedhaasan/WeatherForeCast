package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
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

    override suspend fun getCurrentLocalWeather(): CurrentWeatherEntity {
        return local.getCurrentWeather()
    }

    override suspend fun insertCurrentLocalWeather(c_weather: CurrentWeatherEntity): Long {
        return local.insertCurrentWeather(c_weather)
    }

    override suspend fun deleteCurrentLocalWeather(c_weather: CurrentWeatherEntity): Int {
        return local.deleteCurrentWeather(c_weather)
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        unit: String
    ): FiveDayResponse? {
        return remote.getFiveDayWeather(lat, lon, unit)
    }
}