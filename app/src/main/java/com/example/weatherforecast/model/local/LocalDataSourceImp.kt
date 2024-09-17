package com.example.weatherforecast.model.local

import com.example.weatherforecast.model.database.WeatherDao
import com.example.weatherforecast.model.pojos.WeatherEntity
import com.example.weatherforecast.model.pojos.WeatherResponse


// start in local weather data
class LocalDataSourceImp(val dao: WeatherDao) :LocalDataSourceContract {
    override suspend fun insertCurrentWeather(c_weather: WeatherEntity): Long {
        return  dao.insertCurrentWeather(c_weather)
    }

    override suspend fun deleteCurrentWeather(c_weather: WeatherEntity): Int {
       return dao.deleteCurrentWeather(c_weather)
    }

    override suspend fun getCurrentWeather(): WeatherEntity {
        return dao.getCurrentWeather()
    }
}