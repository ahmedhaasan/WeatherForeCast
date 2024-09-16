package com.example.weatherforecast.model.remote

import WeatherResponse

interface RemoteDataSourceContract {
   suspend fun getCurrentWeather(lat :Double , lon :Double , unit :String) :WeatherResponse?
}