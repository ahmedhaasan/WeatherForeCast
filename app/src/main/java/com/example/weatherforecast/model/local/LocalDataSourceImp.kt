package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.database.WeatherDao
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity


// start in local weather data
class LocalDataSourceImp(val dao: WeatherDao) :LocalDataSourceContract {
    override suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long {
        return  dao.insertCurrentWeather(c_weather)
    }

    override suspend fun deleteCurrentWeather(c_weather: CurrentWeatherEntity): Int {
       return dao.deleteCurrentWeather(c_weather)
    }

    override suspend fun getCurrentWeather(): CurrentWeatherEntity {
        return dao.getCurrentWeather()
    }
}