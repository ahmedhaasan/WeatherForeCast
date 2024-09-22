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
                response.body() // Return the weather response body
            } else {
                null
            }
        } catch (e: UnknownHostException) {
            // Handle network issues like no internet connection or DNS failure
            // Log the error or show a "No internet connection" dialog
            null
        } catch (e: Exception) {
            // Handle other exceptions
            e.printStackTrace() // Log or handle other exceptions accordingly
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
                response.body() // Return the 5-day weather response body
            } else {
                // Handle error responses from the API (e.g., 4xx or 5xx)
                null
            }
        } catch (e: UnknownHostException) {
            // Handle network issues like no internet connection or DNS failure
            // Log the error or show a "No internet connection" dialog
            null
        } catch (e: Exception) {
            // Handle other exceptions
            e.printStackTrace() // Log or handle other exceptions accordingly
            null
        }
    }
}
