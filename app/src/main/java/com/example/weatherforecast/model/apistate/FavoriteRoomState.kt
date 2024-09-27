package com.example.weatherforecast.model.apistate

import com.example.weatherforecast.model.pojos.DailyWeather
import com.example.weatherforecast.model.pojos.Favorite

sealed class FavoriteRoomState {

    class Success(val favorites: List<Favorite>) : FavoriteRoomState()
    class Failure(val msg: Throwable) : FavoriteRoomState()
    class Loading() : FavoriteRoomState()
    class Empty() :FavoriteRoomState()
}