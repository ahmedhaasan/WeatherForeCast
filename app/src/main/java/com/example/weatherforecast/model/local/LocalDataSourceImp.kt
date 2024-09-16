package com.example.weatherforecast.model.local

import WeatherDao
import WeatherResponse

// start in local weather data
class LocalDataSourceImp(val dao:WeatherDao) :LocalDataSourceContract {
    override suspend fun insertCurrentWeather(c_weather: WeatherResponse): Long {
        return  dao.insertCurrentWeather(c_weather)
    }

    override suspend fun deleteCurrentWeather(c_weather: WeatherResponse): Int {
       return dao.deleteCurrentWeather(c_weather)
    }

    override suspend fun getCurrentWeather(): WeatherResponse {
        return dao.getCurrentWeather()
    }
}