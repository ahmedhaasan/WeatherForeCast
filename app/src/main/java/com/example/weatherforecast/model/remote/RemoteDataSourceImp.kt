package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.connection.RetrofitHelper
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse


// first function in the remote dataSource
class RemoteDataSourceImp : RemoteDataSourceContract {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String
    ): WeatherResponse? {
        val response = RetrofitHelper.service.getCurrentWeather(lat, lon, unit)
        return response.body()
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        unit: String
    ): FiveDayResponse? {
        val response = RetrofitHelper.service.getFiveDayWeather(lat, lon, unit)
        return response.body()
    }
}