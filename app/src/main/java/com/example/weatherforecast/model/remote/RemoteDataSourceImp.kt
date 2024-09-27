package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.connection.RetrofitHelper
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
import java.net.UnknownHostException

// first function in the remote dataSource
class RemoteDataSourceImp : RemoteDataSourceContract {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang:String,
        unit: String
    ): WeatherResponse? {
        return try {
            val response = RetrofitHelper.service.getCurrentWeather(lat, lon,lang, unit)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: UnknownHostException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getFiveDayWeather(
        lat: Double,
        lon: Double,
        lang:String,
        unit: String
    ): FiveDayResponse? {
        return try {
            val response = RetrofitHelper.service.getFiveDayWeather(lat, lon,lang, unit)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: UnknownHostException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
