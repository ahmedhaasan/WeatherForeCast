package com.example.weatherforecast.view.homefragment.daily

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.ItemDailyBinding
import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.setIcon
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
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

            // Get the day of the week in full format
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

            // Set the day of the week and weather status
            holder.itemBinding.tvDay.text = if (position == 0) "Tomorrow" else dayOfWeek
            holder.itemBinding.tvStatus.text = current.weatherStatus

            // Set the icon
            setIcon(current.icon, holder.itemBinding.ivIcon)

            // Format the temperatures
            holder.itemBinding.tvDegree.text = "${current.maxTemp.toInt()}°/${current.minTemp.toInt()}°"
        }
    }
}
