package com.example.weatherforecast.model.reposiatory

import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.local.LocalDataSourceContract
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.pojos.Favorite
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


    // create lists
    val fakeFavoriteList = listOf(favorite1, favorite2, favorite3, favorite4)
    val fakeAlarmList = listOf(alarm1, alarm2)


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
        // when part
        // Act
        // frist here collect the frist value and completes the flow
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


}