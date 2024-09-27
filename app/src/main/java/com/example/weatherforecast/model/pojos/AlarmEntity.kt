package com.example.weatherforecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val time: Long,
    val type: String, // 'notification' or 'alarm'
    val latitude: Double,
    val longitude: Double,
    val zoneName: String = ""
) : Serializable // <- Implement Serializable
