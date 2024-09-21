package com.example.weatherforecast.model.view_models.favorite

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Constants
import com.example.weatherforecast.mapDailyWeather
import com.example.weatherforecast.mapHourlyWeatherForToday
import com.example.weatherforecast.mapWeatherResponseToEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *      this is the viwModel for Favorites
 */
class FavoriteViewModel(val repo: ReposiatoryImp) : ViewModel() {

    private val _favorites = MutableLiveData<List<Favorite>>()
    val favorites = _favorites

    private val _currentWeather = MutableLiveData<CurrentWeatherEntity?>()
    val currentWeather = _currentWeather

    private val _dailyWeather = MutableLiveData<List<DailyWeather>>()
    val dailyWeather = _dailyWeather

    private val _hourlyWeather = MutableLiveData<List<HourlyWeather>>()
    val hourlyWeather = _hourlyWeather

    fun getCurrentWeatherRemotly(lat: Double, lon: Double,lang:String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempWeather = repo.getCurrentWeatherRemotely(lat, lon,lang, unit)
            val temp2 = tempWeather?.let { mapWeatherResponseToEntity(it) }
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(temp2)
            }
        }
    }


    // get Hourly
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyWeatherRemotly(lat: Double, lon: Double,lang:String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fiveDayResponse = repo.getFiveDayWeather(lat, lon,lang, unit)
                if (fiveDayResponse != null) {
                    val hourlyWeather: List<HourlyWeather> =
                        mapHourlyWeatherForToday(fiveDayResponse)
                    // Update the UI on the main thread
                    _hourlyWeather.postValue(hourlyWeather)

                }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching hourly weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyWeatherRemotly(lat: Double, lon: Double,lang: String, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fiveDayResponse = repo.getFiveDayWeather(lat, lon,lang, unit)
                if (fiveDayResponse != null) {
                    val tempDailyWeather: List<DailyWeather> = mapDailyWeather(fiveDayResponse)
                    _dailyWeather.postValue(tempDailyWeather)

                }
            } catch (e: Exception) {
                Log.i(Constants.ERROR, "Error fetching daily weather: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun insertFavoriteLocation(fa_location: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavoriteLocation(fa_location)

        }
    }

    fun deleteFavoriteLocation(fav_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavoriteLocation(fav_id)

        }
    }

    fun getAllFavotiteLccations() {
        // collect the favorites from the Flow as the function return a flow
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavoriteLocations().collect { favorites ->
                withContext(Dispatchers.IO) {
                    Log.d("FavoriteFragment", "got lists in viewModel : $favorites")
                    _favorites.postValue(favorites)
                }

            }
        }

    }
}