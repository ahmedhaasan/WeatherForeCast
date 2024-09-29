package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow


interface RemoteDataSourceContract {
   suspend fun getCurrentWeather(lat :Double , lon :Double ,lang :String, unit :String) : Flow<WeatherResponse>
   suspend fun getFiveDayWeather(lat :Double , lon :Double ,lang:String, unit :String):Flow<FiveDayResponse>
}