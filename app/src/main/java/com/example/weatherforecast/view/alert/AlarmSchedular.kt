package com.example.weatherforecast.view.alert

import com.example.weatherforecast.model.pojos.AlarmEntity

interface AlarmScheduler {
    fun create(alarmItem: AlarmEntity)
    fun cancel(alarmItem: AlarmEntity)
}