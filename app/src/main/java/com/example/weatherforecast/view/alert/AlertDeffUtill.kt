package com.example.weatherforecast.view.alert

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.pojos.AlarmEntity

class AlertDeffUtill : DiffUtil.ItemCallback<AlarmEntity>() {
    override fun areItemsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
        return oldItem == newItem
    }
}