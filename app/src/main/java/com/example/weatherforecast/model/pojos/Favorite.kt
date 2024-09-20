package com.example.weatherforecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favoriteLocations")
data class Favorite(
    @PrimaryKey
    val locationName: String,
    val lat: Double,
    val lon: Double
)