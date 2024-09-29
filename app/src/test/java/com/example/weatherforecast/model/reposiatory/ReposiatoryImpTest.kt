package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.local.LocalDataSourceContract
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.Favorite
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.model.remote.RemoteDataSourceContract
import com.example.weatherforecast.model.reposiatory.fakedatasources.FakeLocalDataSource
import com.example.weatherforecast.model.reposiatory.fakedatasources.FakeRemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class ReposiatoryImpTest {


    /**
     *      trying to test three 3 functions in reposiatory
     *      1 - create some fake test classes and lists of these classes
     */

    // create refrances from local and remote DataSource

    lateinit var fakeLocalDataSource: LocalDataSourceContract
    lateinit var fakeRemoteDataSource: RemoteDataSourceContract
    lateinit var repo: ReposiatoryImp

    // for favorites
    val favorite1 = Favorite("giza", 10.10, 10.20)
    val favorite2 = Favorite("cairo", 10.50, 10.40)
    val favorite3 = Favorite("maddi", 10.60, 10.30)
    val favorite4 = Favorite("fesal", 10.70, 10.90)

    // for alarms
    val alarm1 = AlarmEntity(

        246852,
        "notification",
        10.10, 10.90,
        "elmanial"
    )
    val alarm2 = AlarmEntity(

        246852,
        "notification",
        10.10, 10.90,
        "elmanial"
    )

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

    // Create three HourlyWeather objects
    val hourlyWeather1 = HourlyWeather(
        id = 1,
        day = 1695672000L,
        icon = "sunny",
        temperature = 25.0
    )

    val hourlyWeather2 = HourlyWeather(
        id = 2,
        day = 1695675600L,
        icon = "cloudy",
        temperature = 22.0
    )

    val hourlyWeather3 = HourlyWeather(
        id = 3,
        day = 1695679200L,
        icon = "rainy",
        temperature = 18.5
    )

    // Create a list of the HourlyWeather objects
    val hourlyWeatherList = mutableListOf(hourlyWeather1, hourlyWeather2, hourlyWeather3)


    // create lists
    val fakeFavoriteList = mutableListOf(favorite1, favorite2, favorite3, favorite4)
    val fakeAlarmList = mutableListOf(alarm1, alarm2)


    // setUP
    @Before
    fun intializeObjects() {
        fakeLocalDataSource = FakeLocalDataSource(fakeFavoriteList, fakeAlarmList, fakeCurrentWeather)
        fakeRemoteDataSource = FakeRemoteDataSource()
        repo = ReposiatoryImp(fakeRemoteDataSource, fakeLocalDataSource)  // take care of this part
    }

    // test to get all favorites
    @Test
    fun getFavorites_listFavoriteParam_gotFlowFavorites() = runTest {

        // frist here collect the frist value and completes the flow

        // when
        val favouritePlacesFlow = repo.getAllFavoriteLocations()
        val favouritePlaces = favouritePlacesFlow.first()

        // then
        Assert.assertEquals(favouritePlaces,fakeFavoriteList)
    }


    // test to get all alarms
    @Test
    fun getAlarms_listOfAlarms_gotFlowAlarms() = runTest{
        // when
        val alarmsFlow = repo.getAllAlarms()
        val alarmResults = alarmsFlow.first()
        // then
        Assert.assertEquals(alarmResults,fakeAlarmList)
    }


    // test to get the current weather

    @Test
    fun getCurrentWeather_oneCurrent_gotOneCurrent() = runTest{

        // when
        val currentFlow = repo.getCurrentLocalWeather()
        val weather = currentFlow.first()
        // then part
        Assert.assertEquals(weather,fakeCurrentWeather)
    }

    @Test
    fun getHourly_noInsert_GotFailed() = runTest {
        // When: No data is inserted
        val result = repo.getHourlyWeatherLocally().first() // without passing any data

        // Then: Assert that the result is an empty list
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun gotHourly_insert_assertEquallity() = runTest{
        // when
        repo.insertHourlyWeatherLocally(hourlyWeatherList)

        val result = repo.getHourlyWeatherLocally().first()
        // then
        Assert.assertEquals(result,hourlyWeatherList)

    }

}