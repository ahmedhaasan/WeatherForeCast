package com.example.weatherforecast.model.apistate

import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.FiveDayResponse

/**
 *      createing sealed classes so i can use it to show the weather
 *      Status and applay actions
 */
sealed class WeatherApiState {

    class Success(val currentWeather: CurrentWeatherEntity) : WeatherApiState()
    class Failure(val msg: Throwable) : WeatherApiState()
    class Loading() : WeatherApiState()

}
