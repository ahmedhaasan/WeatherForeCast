package com.example.weatherforecast.model.reposiatory

import android.util.Log
import com.example.weatherforecast.model.local.LocalDataSourceContract
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.remote.RemoteDataSourceContract
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 *      applaying Flow and Flow
 */
class ReposiatoryImp(
    private val remote: RemoteDataSourceContract,
    private val local: LocalDataSourceContract
) : ReposiatoryContract {

    // remotly applaying flow
    override suspend fun getCurrentWeatherRemotely(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<WeatherResponse>  {
        return remote.getCurrentWeather(lat, lon, lang, unit)
    }

    // remotely applaying flow
    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<FiveDayResponse>{
        return  remote.getFiveDayWeather(lat,lon,lang,unit)
    }

    override suspend fun getCurrentLocalWeather(): Flow<CurrentWeatherEntity> {
        return local.getCurrentWeather()
    }

    override suspend fun insertCurrentLocalWeather(c_weather: CurrentWeatherEntity): Long {
        return local.insertCurrentWeather(c_weather)
    }


    override suspend fun insertHourlyWeatherLocally(h_weather: List<HourlyWeather>): List<Long> {
        return local.insertHourlyWeatherLocally(h_weather)
    }

    override suspend fun getHourlyWeatherLocally(): Flow<List<HourlyWeather>> {
        return local.getHorlyWeatherLocally()
    }

    override suspend fun insertDailyWeatherLocally(d_weather: List<DailyWeather>): List<Long> {
        return local.insertDailyWeatherLocally(d_weather)
    }

    override suspend fun getDailyWeatherLocally(): Flow<List<DailyWeather>> {
        return local.getDailyWeatherLocally()
    }

    // delete locally
    override suspend fun deleteCurrentLocalWeather(): Int {
        return local.deleteCurrentWeather()
    }


    override suspend fun deleteHourlyWeather(): Int {
        return local.deleteHourlyWeather()
    }

    override suspend fun deleteDailyWeatehr(): Int {
        return local.deleteDailyWeatehr()
    }

    /**
     *  working on favorite
     */

    override suspend fun insertFavoriteLocation(fav_location: Favorite): Long {
        return local.insertFavoriteLocation(fav_location)
    }

    override     suspend fun deleteFavoriteLocation(favorite:Favorite):Int {
        return local.deleteFavoriteLocation(favorite)
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        return local.getAllFavoriteLocations()
    }


    /**
     *
     */
    override suspend fun insertAlarmLocally(alarm: AlarmEntity) {
        local.insertAlarm(alarm)
    }

    override  suspend fun deleteAlarm(alarm: AlarmEntity):Int {
        return local.deleteAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        val alarms = local.getAllAlarms()
        return alarms
    }
}