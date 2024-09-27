package com.example.weatherforecast.model.apistate

import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.DailyWeather

sealed class AlarmState {

    class Success(val alarms: List<AlarmEntity>) : AlarmState()
    class Failure(val msg: Throwable) : AlarmState()
    class Loading() : AlarmState()
    class Empty() :AlarmState()

}