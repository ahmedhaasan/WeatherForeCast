package com.example.weatherforecast.view.alert.view

import com.example.weatherforecast.formatMillisToDateTimeString


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.AlertItemBinding
import com.example.weatherforecast.model.pojos.AlarmEntity

class AlarmAdapter : ListAdapter<AlarmEntity, AlarmAdapter.AlarmViewHolder>(AlertDeffUtill()) {

    inner class AlarmViewHolder( val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)

        holder.binding.tvKind.text = alarm.type
        holder.binding.tvZoneName.text = alarm.zoneName

        holder.binding.tvFromDate.text =
            formatMillisToDateTimeString(alarm.time, "dd MMM yyyy")

        holder.binding.tvFromTime.text =
            formatMillisToDateTimeString(alarm.time, "hh:mm a")
    }


}



