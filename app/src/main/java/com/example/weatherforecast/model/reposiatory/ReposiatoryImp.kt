package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.pojos.WeatherResponse
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *      applaying Flow and Flow
 */
class ReposiatoryImp(
    private val remote: RemoteDataSourceImp,
    private val local: LocalDataSourceImp
) : ReposiatoryContract {

    // remotly applaying flow
    override suspend fun getCurrentWeatherRemotely(
        lat: Double,
        lon: Double,
        lang:String,
        unit: String
    ): Flow<WeatherResponse> {
        return flow {
           val current =  remote.getCurrentWeather(lat, lon,lang, unit)
            current?.let { emit(it) } // Emit the list of products
        }
    }

    // remotely applaying flow
    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang:String,
        unit: String
    ): Flow<FiveDayResponse>? {
        return flow {
            val fiveWeather = remote.getFiveDayWeather(lat, lon,lang, unit)
            fiveWeather?.let { emit(it) }
        }
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

    override suspend fun deleteFavoriteLocation(fav_id: String) {
        return local.deleteFavoriteLocation(fav_id)
    }

    override suspend fun getAllFavoriteLocations(): Flow<List<Favorite>> {
        return local.getAllFavoriteLocations()
    }
}