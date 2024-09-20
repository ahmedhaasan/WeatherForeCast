package com.example.weatherforecast.model.checknetwork

interface NetworkChangeListener {
    fun onNetworkChanged(isConnected: Boolean)
}
