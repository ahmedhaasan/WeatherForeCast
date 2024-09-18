package com.example.weatherforecast.view.homefragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.Constants
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.checknetwork.NetworkChangeListener
import com.example.weatherforecast.model.checknetwork.NetworkChangeReceiver
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_model.WeatherViewModel
import com.example.weatherforecast.model.view_model.WeatherViewModelFactory
import com.example.weatherforecast.setIcon
import com.example.weatherforecast.view.homefragment.daily.DailyAdapter
import com.example.weatherforecast.view.homefragment.hourly.HourlyAdapter
import com.google.android.gms.location.*
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment(), NetworkChangeListener {

    lateinit var binding: FragmentHomeBinding
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var networkChangeReceiver: NetworkChangeReceiver


    // lat and long
    var lat: Double = 0.0
    var lon: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // register the BroadCast here to listen for network Changes
        networkChangeReceiver = NetworkChangeReceiver(this)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireActivity().registerReceiver(networkChangeReceiver, intentFilter)

    }

    // un register the listener
    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(networkChangeReceiver)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())

        checkLocationStatus()
        binding.allowLocationButton.setOnClickListener {
            enableLocationServices()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLocationStatus() {
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                getFreshLocation()
                loadWeatherData()
                updateUI(true) // Show weather data and hide permission layout
            } else {
                updateUI(false) // Show permission layout and hide weather data
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.My_LOCATION_PERMISSION_ID
            )
        }
    }

    private fun updateUI(isLocationEnabled: Boolean) {
        if (isLocationEnabled) {
            binding.homeScrollView.visibility = View.VISIBLE
            binding.permissionConstrain.visibility = View.GONE
        } else {
            binding.homeScrollView.visibility = View.GONE
            binding.permissionConstrain.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadWeatherData() {
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = WeatherViewModelFactory(repo)
        val viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        viewModel.getCurrentWeatherRemotly(lat, lon, Constants.METRIC_UNIT)
        viewModel.getCurrentWeatherLocally()  // get the data locally if no network
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { weather ->
            weather?.let {
                val decimalFormat = DecimalFormat("#.##")
                binding.tvLocationName.text = weather.city

                val dateTime = Instant.ofEpochSecond(weather.date.toLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM yyyy", Locale.ENGLISH)
                binding.tvCurrentDate.text = dateTime.format(dateFormatter)

                binding.tvWeatherStatus.text = weather.weatherStatus
                binding.tvCurrentDegree.text = "${decimalFormat.format(weather.temperature)}°C"
                setIcon(weather.weatherIcon, binding.ivWeatherIcon)
                binding.presser.text = "${decimalFormat.format(weather.pressure)} hPa"
                binding.humidity.text = "${weather.humidity}%"
                binding.wind.text = "${decimalFormat.format(weather.windSpeed)} m/s"
                binding.clouds.text = "${weather.clouds}%"
                binding.visability.text = "${decimalFormat.format(weather.visibility / 1000)} km"
            } ?: run {
                Toast.makeText(requireContext(), "No current weather available", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        hourlyAdapter = HourlyAdapter(emptyList())
        binding.hoursRecycler.apply {
            adapter = hourlyAdapter
            // Set the layout manager to horizontal
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // try observe on hourly data
        viewModel.getHourlyWeather(lat, lon, Constants.METRIC_UNIT)
        viewModel.hourlyWeather.observe(viewLifecycleOwner, Observer { hourlyWeather ->
            if (hourlyWeather != null && hourlyWeather.isNotEmpty()) {
                hourlyAdapter.submitList(hourlyWeather)

                Log.i(Constants.SUCCESS, "HOURLY FETCHED successfuly $hourlyWeather")
            } else {
                Log.i(Constants.ERROR, "Error fetch Hourly")

            }
        })

        dailyAdapter = DailyAdapter(emptyList())
        binding.daysRecyclerView.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
        // observe on daily data and check it
        viewModel.getDailyWeather(lat, lon, Constants.METRIC_UNIT)
        viewModel.dailyWeather.observe(viewLifecycleOwner, Observer { mapDailyWeather ->
            if (mapDailyWeather != null && mapDailyWeather.isNotEmpty()) {
                dailyAdapter.submitList(mapDailyWeather)
                Log.i(Constants.SUCCESS, "Daily FETCHED successfuly $mapDailyWeather")
            } else {
                Log.i(Constants.ERROR, "Error fetch Hourly")


            }
        })

    }

    // Check permissions
    private fun checkPermissions(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Check if location services are enabled
    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Prompt the user to enable location services
    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    // Get the user's fresh location
    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        val locationRequest = LocationRequest.Builder(0).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build()

        fusedLocation.requestLocationUpdates(locationRequest, object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lat = locationResult.locations[0].latitude
                lon = locationResult.locations[0].longitude
                // Reload UI with the new location data
                loadWeatherData()
            }
        }, Looper.myLooper())
    }

    // when comeBack to the same home fragment again
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        // Check again if the location is enabled
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                getFreshLocation()
                loadWeatherData()
                binding.homeScrollView.visibility = View.VISIBLE
                binding.permissionConstrain.visibility = View.GONE
            } else {
                binding.homeScrollView.visibility = View.GONE
                binding.permissionConstrain.visibility = View.VISIBLE
            }
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.My_LOCATION_PERMISSION_ID
            )
        }
    }

    override fun onNetworkChanged(isConnected: Boolean) {

    }
}
