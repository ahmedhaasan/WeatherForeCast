package com.example.weatherforecast.model.reposiatory.fakedatasources

import com.example.weatherforecast.model.apistate.AlarmState
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.local.LocalDataSourceContract
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow

class FakeLocalDataSource(
    val favorites: List<Favorite>,
    val alarms: List<AlarmEntity>,
    currentWeather : CurrentWeatherEntity
) :
    LocalDataSourceContract {

    /**
     *      intialize the functions that i will test with
     *      i've used just tow functions only
     */

    //private val fakeFavorites = MutableStateFlow(favorites)
    private val fakeFavorites = MutableStateFlow<List<Favorite>>(favorites)

    private val fakeAlarms = MutableStateFlow(alarms)
    private  val fakeCurrentWeather = MutableStateFlow(currentWeather)


    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return fakeAlarms
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {

        return fakeFavorites
    }

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity> {
        return fakeCurrentWeather
    }


    //////////////
    override suspend fun insertCurrentWeather(c_weather: CurrentWeatherEntity): Long {
        TODO("Not yet implemented")
    }



    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getHorlyWeatherLocally(): Flow<List<HourlyWeather>> {
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

    override suspend fun deleteCurrentWeather(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDailyWeatehr(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteLocation(fav_id: String) {
        TODO("Not yet implemented")
    }



    override suspend fun insertAlarm(alarm: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity): Int {
        TODO("Not yet implemented")
    }


}