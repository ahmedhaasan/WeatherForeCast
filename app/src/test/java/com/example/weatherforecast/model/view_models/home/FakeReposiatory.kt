package com.example.weatherforecast.model.view_models.home

import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 *      here in this exmple i will test two functions
 */
class FakeReposiatory(
    var currentWeather: CurrentWeatherEntity,
    val hourlyList: List<HourlyWeather>
) : ReposiatoryContract {

    val fakeCurrentWeater : Flow<CurrentWeatherEntity> = emptyFlow()
    val fakeHourlyWeather : Flow<List<HourlyWeather>> = emptyFlow()

    override suspend fun getCurrentLocalWeather(): Flow<CurrentWeatherEntity> {

        return flowOf(currentWeather) // will return flow
    }


    override suspend fun getHourlyWeatherLocally(): Flow<List<HourlyWeather>> {
        return flowOf(hourlyList)
    }

    override suspend fun getCurrentWeatherRemotely(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<WeatherResponse>? {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<FiveDayResponse>? {
        TODO("Not yet implemented")
    }


    override suspend fun insertCurrentLocalWeather(c_weather: CurrentWeatherEntity): Long {
        TODO("Not yet implemented")


    }

    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        TODO("Not yet implemented")
    }


    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHourlyWeather(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDailyWeatehr(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentLocalWeather(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteLocation(fav_id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarmLocally(alarm: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity): Int {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        TODO("Not yet implemented")
    }
}