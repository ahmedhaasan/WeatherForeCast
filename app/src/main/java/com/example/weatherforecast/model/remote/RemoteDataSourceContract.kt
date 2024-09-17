package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.pojos.WeatherResponse


interface RemoteDataSourceContract {
   suspend fun getCurrentWeather(lat :Double , lon :Double , unit :String) : WeatherResponse?
}