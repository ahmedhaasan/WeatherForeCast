package com.example.weatherforecast.model.view_models.alarm

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.model.apistate.AlarmState
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.reposiatory.ReposiatoryContract
import com.example.weatherforecast.model.view_models.home.FakeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.Equals


/**
 *      here im trying to test the alarm view Model
 */
@RunWith(AndroidJUnit4::class)
class AlarmViewModelTest {

    // given

    lateinit var repository: ReposiatoryContract
    lateinit var alarmViewModel: AlarmViewModel

    // some alarm objects
    val alarm1 = AlarmEntity(
        time = 1696003200000L,
        type = "notification",
        latitude = 34.0522,
        longitude = -118.2437,
        zoneName = "Los Angeles"
    )

    val alarm2 = AlarmEntity(
        time = 1696089600000L,
        type = "alarm",
        latitude = 40.7128,
        longitude = -74.0060,
        zoneName = "New York"
    )

    val alarm3 = AlarmEntity(
        time = 1696176000000L,
        type = "notification",
        latitude = 51.5074,
        longitude = -0.1278,
        zoneName = "London"
    )

    val alarm4 = AlarmEntity(
        time = 1696262400000L,
        type = "alarm",
        latitude = 35.6895,
        longitude = 139.6917,
        zoneName = "Tokyo"
    )

    // setUp function
    @Before
    fun setUP(){
        repository = FakeRepository()
        alarmViewModel = AlarmViewModel(repository)
    }

    @Test
    fun insertAlarm_passArgument_assertInsertion() = runTest{

        // when
        alarmViewModel.insertAlarmLocally(alarm1)
        // insert again
        alarmViewModel.insertAlarmLocally(alarm2)

        val twoAlarms = listOf(alarm1,alarm2)
        // then assert
        alarmViewModel.getAllAlarmsLocally()

       val alarmFlow =  alarmViewModel.alarmsStateFlow.first{it is AlarmState.Success}

        if (alarmFlow is AlarmState.Success) {
            assertEquals(alarmFlow.alarms.size,twoAlarms.size)

            assertEquals(alarmFlow.alarms[0],alarm1,) // check if frist equal frist

        }

    }

    @Test
    fun deleteAlarm_passParam_assertDeletion() = runTest {
        alarmViewModel.insertAlarmLocally(alarm1)
        alarmViewModel.insertAlarmLocally(alarm2) // Assuming alarm2 is also present for this test

        // Act: Delete the alarm
        alarmViewModel.deleteAlarmLocally(alarm1)

        // Assert: Check if alarm has been deleted
        alarmViewModel.getAllAlarmsLocally()
        val alarmFlow = alarmViewModel.alarmsStateFlow.first { it is AlarmState.Success }

        if (alarmFlow is AlarmState.Success) {
            assertEquals(alarmFlow.alarms.size, 1) // Assert size is now 1
            assertEquals(alarmFlow.alarms[0], alarm2)
        }
    }

}