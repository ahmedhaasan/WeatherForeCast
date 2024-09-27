package com.example.weatherforecast.model.view_models.home

import com.example.weatherforecast.model.apistate.HourlyApiState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


/**
 *      starting  to test ViewModel
 */
class WeatherViewModelTest {


    // declare the repo and the viewModel
    // Givin
    lateinit var reposiatory: ReposiatoryContract
    lateinit var viewModel: WeatherViewModel

    // for current Weatehr
    val fakeCurrentWeather = CurrentWeatherEntity(
        city = "New York",
        temperature = 25.3,
        weatherStatus = "Clear",
        weatherIcon = "01d",
        date = System.currentTimeMillis(),  // current date/time in milliseconds
        lat = 40.7128,
        lon = -74.0060,
        windSpeed = 5.6,
        pressure = 1012,
        humidity = 60,
        clouds = 0,
        visibility = 10000
    )

    // for hourly weather

    val hourlyWeather = HourlyWeather(11, 223333, "2h", 2.50)
    val hourlyWeather2 = HourlyWeather(12, 203050, "6d", 2.80)
    val hourlyWeather3 = HourlyWeather(13, 708060, "8f", 2.5)
    val hourlyWeather4 = HourlyWeather(14, 9080, "8f", 20.30)

    val hourlyList = listOf(hourlyWeather,hourlyWeather2,hourlyWeather3,hourlyWeather4)

    // now setUp the instances
    @Before
    fun setUp(){
        reposiatory = FakeReposiatory(fakeCurrentWeather,hourlyList)
        viewModel = WeatherViewModel(reposiatory)
    }

    @Test
    fun getCurrentLocalWeather_passCurrent_gotCurrent() = runTest{

        // when
        viewModel.getCurrentWeatherLocally()
        // then
        viewModel.currentWeatherState.collect{ result ->

            Assert.assertEquals(WeatherApiState.Success(fakeCurrentWeather),result)

        }
    }

    // Test for hourly weather
    @Test
    fun getHourlyWeatherLocally_passHourly_gotHourly() = runTest {
        // Trigger the function
        viewModel.getHourlyWeatherLocally()

        // Collect the state and assert
        viewModel.hourlyWeatherState.collect { result ->
            Assert.assertEquals(HourlyApiState.Success(hourlyList), result)
        }
    }
}

