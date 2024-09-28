package com.example.weatherforecast.model.view_models.favorite

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Constants
import com.example.weatherforecast.mapDailyWeather
import com.example.weatherforecast.mapHourlyWeatherForTwoDays
import com.example.weatherforecast.mapWeatherResponseToEntity
import com.example.weatherforecast.model.apistate.DailyApiState
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.apistate.HourlyApiState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *      this is the viwModel for Favorites
 */
class FavoriteViewModel(val repo: ReposiatoryContract) : ViewModel() {

    private val _favoriteState = MutableStateFlow<FavoriteRoomState>(FavoriteRoomState.Empty())
    val favoriteState = _favoriteState

    private val _currentWeatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading())
    val currentWeatherState = _currentWeatherState

    private val _dailyWeatherState = MutableStateFlow<DailyApiState>(DailyApiState.Loading())
    val dailyWeatherState = _dailyWeatherState

    private val _hourlyWeatherState = MutableStateFlow<HourlyApiState>(HourlyApiState.Loading())
    val hourlyWeatherState = _hourlyWeatherState

    fun getCurrentWeatherRemotly(lat: Double, lon: Double, lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCurrentLocalWeather()  // delete the current weather frist
            repo.getCurrentWeatherRemotely(lat, lon, lang, unit)
                ?.catch { e -> _currentWeatherState.value = WeatherApiState.Failure(e) }
                // Take the CurrentWeather From The Response
                ?.map { weatherResponse -> mapWeatherResponseToEntity(weatherResponse) }
                ?.collect { weather ->
                    _currentWeatherState.value = WeatherApiState.Success(weather)
                    repo.insertCurrentLocalWeather(weather) // the insert the new one
                }
        }
    }


    // get Hourly
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyWeatherRemotly(lat: Double, lon: Double, lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Clear the DB before adding new data
                repo.deleteHourlyWeather()
                repo.getFiveDayWeather(lat, lon, lang, unit)
                    ?.catch { error -> _hourlyWeatherState.value = HourlyApiState.Failure(error) }
                    ?.map { fiveDaily -> // map the five Daily into Hourly
                        mapHourlyWeatherForTwoDays(fiveDaily)
                    }?.collect { hourly ->
                        _hourlyWeatherState.value = HourlyApiState.Success(hourly)
                        repo.insertHourlyWeatherLocally(hourly) // then add the new one
                    }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching hourly weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     *      get Daily weather
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyWeatherRemotly(lat: Double, lon: Double, lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Clear the DB before adding new data
                repo.deleteDailyWeatehr()
                repo.getFiveDayWeather(lat, lon, lang, unit)
                    ?.catch { error -> _dailyWeatherState.value = DailyApiState.Failure(error) }
                    ?.map { fiveDaily -> mapDailyWeather(fiveDaily) }
                    ?.collect { daily -> _dailyWeatherState.value = DailyApiState.Success(daily) }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching daily weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    // locally
    fun insertFavoriteLocation(fa_location: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavoriteLocation(fa_location)

        }
    }

    fun deleteFavoriteLocation(favorite:Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavoriteLocation(favorite)

        }
    }

    fun getAllFavotiteLccations() {
        // collect the favorites from the Flow as the function return a flow
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavoriteLocations()
                .catch { error -> _favoriteState.value = FavoriteRoomState.Failure(error) }
                .onEmpty {
                    _favoriteState.value = FavoriteRoomState.Empty()
                }
                .collect { favorites ->
                    _favoriteState.value = FavoriteRoomState.Success(favorites)
                }
        }

    }
}