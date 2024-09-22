package com.example.weatherforecast.model.apistate

import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather

sealed class HourlyApiState {

    class Success(val dailyWeatehr: List<HourlyWeather>) : HourlyApiState()
    class Failure(val msg: Throwable) : HourlyApiState()
    class Loading() : HourlyApiState()
}