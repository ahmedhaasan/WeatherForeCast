package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.connection.RetrofitHelper
import com.example.weatherforecast.model.connection.map.retrofit
import com.example.weatherforecast.model.pojos.FiveDayResponse
import com.example.weatherforecast.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.UnknownHostException

// first function in the remote dataSource
class RemoteDataSourceImp : RemoteDataSourceContract {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang:String,
        unit: String
    ): Flow<WeatherResponse> = flow{
         try {
            val response = RetrofitHelper.service.getCurrentWeather(lat, lon,lang, unit)
            if (response.isSuccessful) {
                response.body()?.let { emit(it) }  // return flow
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
    ): Flow<FiveDayResponse> = flow {
         try {
            val response = RetrofitHelper.service.getFiveDayWeather(lat, lon,lang, unit)
            if (response.isSuccessful) {
                response.body()?.let { emit(it) }   // return flow
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
