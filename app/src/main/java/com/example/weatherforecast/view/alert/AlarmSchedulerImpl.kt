package com.example.weatherforecast.view.alert

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import com.example.weatherforecast.Constants
import com.example.weatherforecast.model.pojos.AlarmEntity

class AlarmSchedulerImpl(private val application: Application) : AlarmScheduler {
    private val alarmManager = application.getSystemService(AlarmManager::class.java)

    /**
     *      this class Alarm SchedularImp has Two function s create and calcel  alarm
     */

    @SuppressLint("ScheduleExactAlarm")
    override fun create(alarmItem: AlarmEntity) {
        val intent = Intent(application, AlarmReceiver::class.java)
        intent.apply {
            putExtra(
                Constants.WEATHER_ALARM,
                alarmItem
            )  // note put extra take Serializable objects or parcelable
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.time.toInt().toLong(),
            PendingIntent.getBroadcast(
                application,
                alarmItem.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    }


    override fun cancel(alarmItem: AlarmEntity) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                application,
                alarmItem.hashCode(),
                Intent(application, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}