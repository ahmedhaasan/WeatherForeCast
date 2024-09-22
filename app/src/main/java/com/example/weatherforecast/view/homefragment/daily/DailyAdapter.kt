package com.example.weatherforecast.view.homefragment.daily

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ItemDailyBinding
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.setIcon
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.DayOfWeek
import java.util.Locale

class DailyAdapter(val dailyWeather: List<DailyWeather>) :
    ListAdapter<DailyWeather, DailyAdapter.DailyViewHolder>(DailyDeffUtil()) {

    class DailyViewHolder(var itemBinding: ItemDailyBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDailyBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val current: DailyWeather? = getItem(position)

        if (current != null) {
            // Convert timestamp to LocalDate
            val date = Instant.ofEpochSecond(current.day)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // Map the day of the week to string resources
            val context = holder.itemBinding.root.context
            val dayOfWeekResourceId = when (date.dayOfWeek) {
                DayOfWeek.SUNDAY -> R.string.sunday
                DayOfWeek.MONDAY -> R.string.monday
                DayOfWeek.TUESDAY -> R.string.tuesday
                DayOfWeek.WEDNESDAY -> R.string.wednesday
                DayOfWeek.THURSDAY -> R.string.thursday
                DayOfWeek.FRIDAY -> R.string.friday
                DayOfWeek.SATURDAY -> R.string.saturday
            }

            // Set the day of the week and weather status
            holder.itemBinding.tvDay.text = if (position == 0) {
                context.getString(R.string.tomorrow)
            } else {
                context.getString(dayOfWeekResourceId)
            }

            holder.itemBinding.tvStatus.text = current.weatherStatus

            // Set the icon
            setIcon(current.icon, holder.itemBinding.ivIcon)

            // Format the temperatures  , min and max degree
            holder.itemBinding.tvDegree.text = "${current.maxTemp.toInt()}°/${current.minTemp.toInt()}°"
        }
    }
}
