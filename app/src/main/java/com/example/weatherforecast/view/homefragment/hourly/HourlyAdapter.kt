package com.example.weatherforecast.view.homefragment.hourly

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ItemHoursBinding
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.setIcon
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class HourlyAdapter(private val hourlyWeather: List<HourlyWeather>) :
    ListAdapter<HourlyWeather, HourlyAdapter.HourlyViewHolder>(HourlyDeffUtil()) {

    inner class HourlyViewHolder(var itemBinding: ItemHoursBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHoursBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val current: HourlyWeather? = getItem(position)
        if (current != null) {
            // Convert the timestamp to LocalDateTime
            val localDateTime = Instant.ofEpochSecond(current.day)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            // Extract the hour and format it
            val hour = localDateTime.hour
            val context = holder.itemBinding.root.context
            val formattedHour = String.format(context.getString(R.string.hour_format), hour)

            // Extract the day of the week
            val dayOfWeek = localDateTime.dayOfWeek.getDisplayName(
                java.time.format.TextStyle.SHORT, Locale.getDefault()
            )

            // Set the day of the week and formatted hour in separate TextViews
            holder.itemBinding.tvDay.text = dayOfWeek
            holder.itemBinding.tvTimeHours.text = formattedHour

            // Set the icon and temperature
            setIcon(current.icon, holder.itemBinding.ivStatusIcon)
            holder.itemBinding.tvDegree.text = "${current.temperature}°"
        }
    }
}
