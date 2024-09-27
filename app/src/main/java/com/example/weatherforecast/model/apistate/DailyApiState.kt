package com.example.weatherforecast.model.apistate

import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.FiveDayResponse

sealed class DailyApiState {

    class Success(val dailyWeatehr: List<DailyWeather>) : DailyApiState()
    class Failure(val msg: Throwable) : DailyApiState()
    class Loading() : DailyApiState()
}