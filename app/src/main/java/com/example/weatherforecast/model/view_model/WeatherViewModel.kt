package com.example.weatherforecast.model.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.pojos.WeatherResponse

import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repo: ReposiatoryImp) : ViewModel() {
    private val _currentWeather = MutableLiveData<WeatherResponse?>()
    val currentWeather = _currentWeather
    // create functions

    /**
     *  take care you will use the courotines here with the viewModelLifeScope
     */

    fun getCurrentWeatherRemotly(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempWeather = repo.getCurrentWeatherRemotely(lat, lon, unit)
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(tempWeather)
            }
        }
    }


/*    // get the currentWeatehrLocally
    fun getCurrentWeatherLocally(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempWeather = repo.getCurrentLocalWeather()
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(tempWeather)
            }
        }
    }

    // insert weather in Db
    fun insertCurrentWeatherLocally(c_weather: WeatherResponse): Long? {
        var result: Long? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = repo.insertCurrentLocalWeather(c_weather)

        }
        return result
    }

    // delete current Weather locally

    fun deleteCurrentWeatherLocally(c_weather: WeatherResponse): Int? {
        var result: Int? = null
        viewModelScope.launch(Dispatchers.IO) {
            result = repo.deleteCurrentLocalWeather(c_weather)

        }
        return result
    }*/

}