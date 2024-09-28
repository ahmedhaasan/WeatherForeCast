package com.example.weatherforecast.model.view_models.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.model.apistate.DailyApiState
import com.example.weatherforecast.model.apistate.HourlyApiState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.HourlyWeather
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WeatherViewModelTest {


    lateinit var currentViewModel: WeatherViewModel
    lateinit var repository: FakeRepository


    // Givin

    val hourlyWeather1 = HourlyWeather(
        id = 1,
        day = 1633024800, // Example timestamp (in seconds)
        icon = "sunny",
        temperature = 25.0
    )

    val hourlyWeather2 = HourlyWeather(
        id = 2,
        day = 1633028400, // Example timestamp (in seconds)
        icon = "cloudy",
        temperature = 22.0
    )

    val hourlyWeather3 = HourlyWeather(
        id = 3,
        day = 1633032000, // Example timestamp (in seconds)
        icon = "rainy",
        temperature = 19.0
    )

    val hourlyWeather4 = HourlyWeather(
        id = 4,
        day = 1633035600, // Example timestamp (in seconds)
        icon = "clear",
        temperature = 23.0
    )

    val hourlyWeatherList = listOf(hourlyWeather1, hourlyWeather2, hourlyWeather3, hourlyWeather4)

    val dailyWeather1 = DailyWeather(
        id = 1,
        day = 1632949200, // Example timestamp (in seconds)
        icon = "sunny",
        minTemp = 18.0,
        maxTemp = 28.0,
        weatherStatus = "Clear Sky"
    )

    val dailyWeather2 = DailyWeather(
        id = 2,
        day = 1633035600, // Example timestamp (in seconds)
        icon = "cloudy",
        minTemp = 16.0,
        maxTemp = 24.0,
        weatherStatus = "Partly Cloudy"
    )

    val dailyWeather3 = DailyWeather(
        id = 3,
        day = 1633122000, // Example timestamp (in seconds)
        icon = "rainy",
        minTemp = 14.0,
        maxTemp = 22.0,
        weatherStatus = "Rain Showers"
    )

    val dailyWeather4 = DailyWeather(
        id = 4,
        day = 1633208400, // Example timestamp (in seconds)
        icon = "stormy",
        minTemp = 12.0,
        maxTemp = 20.0,
        weatherStatus = "Thunderstorm"
    )

    val dailyWeatherList = listOf(dailyWeather1, dailyWeather2, dailyWeather3, dailyWeather4)


    val currentWeather = CurrentWeatherEntity(
        city = "New York",
        temperature = 22.0,
        weatherStatus = "Clear Sky",
        weatherIcon = "sunny",
        date = 1633024800, // Example timestamp (in seconds)
        lat = 40.7128,
        lon = -74.0060,
        windSpeed = 5.0,
        pressure = 1012,
        humidity = 60,
        clouds = 10,
        visibility = 10000
    )

    @Before
    fun setUP() {
        repository = FakeRepository()
        currentViewModel = WeatherViewModel(repository)
    }


    @Test
    fun insertCurrentWeahter_passArgument_assertExistance() = runTest {
        // when
        repository.insertCurrentLocalWeather(currentWeather) // add it in
        currentViewModel.getCurrentWeatherLocally()  // call get from viewModl

        // then assert
        val currentFlow = currentViewModel.currentWeatherState.first {
            it is WeatherApiState.Success
        }

        // actually assert
        if (currentFlow is WeatherApiState.Success) {
            assertEquals(currentFlow.currentWeather, currentWeather)
        }
    }


    /**
     *      test Hourly
     */

    @Test
    fun insertHouly_passArguments_assertExistance() = runTest {

        // when   insert using repo
        repository.insertHourlyWeatherLocally(hourlyWeatherList)

        // then got them using viewModel
        currentViewModel.getHourlyWeatherLocally()

        // then assert When Success Condition
        val hourlyFlow = currentViewModel.hourlyWeatherState.first { it is HourlyApiState.Success }

        if (hourlyFlow is HourlyApiState.Success) {
            assertEquals(hourlyFlow.hourlyWeather, hourlyWeatherList)
        }
    }

    @Test
    fun insertDaily_passArguments_AssertAddition() = runTest {
        // when
        repository.insertDailyWeatherLocally(dailyWeatherList)

        // then calling them using viewModel
        currentViewModel.getDailyWeatehrLocally()
        // then assert them in Success State
        val dailyFlow = currentViewModel.dailyWeatherState.first {
            it is DailyApiState.Success
        }
        // then when Success
        if (dailyFlow is DailyApiState.Success) {
            assertEquals(dailyFlow.dailyWeatehr, dailyWeatherList)
        }
    }

    @Test
    fun deleteDaily_passArguments_assertDeletion() = runTest {

        // Insert the weather to set initial data
        repository.insertDailyWeatherLocally(dailyWeatherList)

        // When: Deleting the daily weather
        repository.deleteDailyWeatehr()


        currentViewModel.getDailyWeatehrLocally()
        val dailyFlow = currentViewModel.dailyWeatherState.first {
            it is DailyApiState.Success || it is DailyApiState.Failure
        }


        if (dailyFlow is DailyApiState.Success) {
            assertTrue(dailyFlow.dailyWeatehr.isEmpty())
        } else if (dailyFlow is DailyApiState.Failure) {

            assertTrue(dailyFlow.msg.message?.contains("null") == true) // if deletion not sucess
        }
    }

    /**
     *      Delete Hourly
     */

    @Test
    fun deleteHourly_passArguments_assertDeletion() = runTest {

        // Insert the weather to set initial data
        repository.insertHourlyWeatherLocally(hourlyWeatherList)

        // When: Deleting the daily weather
        repository.deleteHourlyWeather()

        // Then: Call getDailyWeatehrLocally
        currentViewModel.getHourlyWeatherLocally()

        // Check the state emitted by the view model
        val dailyFlow = currentViewModel.hourlyWeatherState.first {
            it is HourlyApiState.Success || it is HourlyApiState.Failure
        }
        if (dailyFlow is HourlyApiState.Success) {
            assertTrue(dailyFlow.hourlyWeather.isEmpty())
        } else if (dailyFlow is HourlyApiState.Failure) {

            assertTrue(dailyFlow.msg.message?.contains("null") == true)
        }
    }
}