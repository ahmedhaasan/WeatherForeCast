package com.example.weatherforecast.view.homefragment.hourly

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.ItemHoursBinding
import com.example.weatherforecast.model.pojos.HourlyWeather
import com.example.weatherforecast.setIcon
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HourlyAdapter(private val hourlyWeather: List<HourlyWeather>) :
    ListAdapter<HourlyWeather, HourlyAdapter.HourlyViewHolder>(HourlyDeffUtil()) {
    lateinit var binding: ItemHoursBinding

    class HourlyViewHolder(var itemBinding: ItemHoursBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        binding = ItemHoursBinding.inflate(inflater, parent, false)
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

            // Extract the hour
            val hour = localDateTime.hour
            val formattedHour = String.format("%02d:00", hour)

            // Extract the day of the week
            val dayOfWeek = localDateTime.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())

            // Set the hour and day of the week in separate TextViews
            binding.tvDay.text = dayOfWeek
            binding.tvTimeHours.text = formattedHour

            // Set the icon and temperature
            setIcon(current.icon, binding.ivStatusIcon)
            binding.tvDegree.text = "${current.temperature}°"
        }
    }
}
