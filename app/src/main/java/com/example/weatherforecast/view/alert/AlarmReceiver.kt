package com.example.weatherforecast.view.alert

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Ensure the message is not null, return early if null
        val message = intent?.getStringExtra(Constants.WEATHER_ALARM) ?: return
        val channelId = Constants.NOTIFICATION_ID

        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(ctx, channelId.toString())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Weather Alarm")
                .setContentText("Notification sent with message: $message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            // Trigger the notification with an ID
            notificationManager.notify(1, builder.build())
        }
    }
}
