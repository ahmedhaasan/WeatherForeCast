package com.example.weatherforecast.model.remote

import WeatherResponse
import com.example.weatherforecast.model.connection.RetrofitHelper


// first function in the remote dataSource
class RemoteDataSourceImp : RemoteDataSourceContract {
    override suspend fun getCurrentWeather(lat: Double, lon: Double, unit: String) : WeatherResponse? {
        val response = RetrofitHelper.service.getCurrentWeather(lat, lon, unit)
    return response.body()
    }
}