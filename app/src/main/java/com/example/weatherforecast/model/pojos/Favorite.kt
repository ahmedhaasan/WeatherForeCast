package com.example.weatherforecast.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoriteLocations")
data class Favorite (    @PrimaryKey(autoGenerate = true) val id :Int ,val locationName :String)