package com.example.weatherforecast.view.alert

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import com.example.weatherforecast.Constants
import com.example.weatherforecast.model.pojos.AlarmEntity

class AlarmSchedulerImpl private constructor(private val application: Application) :
    AlarmScheduler {
    private val alarmManager: AlarmManager by lazy {
        application.getSystemService(AlarmManager::class.java)
    }

    /**
     *      this class Alarm SchedularImp has Two function s create and calcel  alarm
     *      we make it singltone class
     */

    companion object {
        private var instance: AlarmSchedulerImpl? = null
        fun getInstance(application: Application): AlarmSchedulerImpl =
            instance ?: synchronized(this) {
                instance ?: AlarmSchedulerImpl(application).also {
                    instance = it
                }
            }
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun create(alarmItem: AlarmEntity) {
        /* // Check if the OS version is Android 12 or higher and if exact alarm permission is granted
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
             if (!alarmManager.canScheduleExactAlarms()) {
                 val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                 application.startActivity(intent)
                 return // Exit early if permission is not granted
             }
         }*/

        val intent = Intent(application, AlarmReceiver::class.java)
        intent.apply {
            putExtra(
                Constants.WEATHER_ALARM,
                alarmItem
            )  // note put extra take Serializable objects or parcelable
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.time,
            PendingIntent.getBroadcast(
                application,
               alarmItem.time.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    }

    override fun cancel(alarmItem: AlarmEntity) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                application,
                alarmItem.time.toInt(),
                Intent(application, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}