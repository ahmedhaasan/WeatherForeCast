package com.example.weatherforecast.view.homefragment

import android.Manifest
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
import androidx.lifecycle.ViewModel
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
import com.example.weatherforecast.model.view_models.home.WeatherViewModel
import com.example.weatherforecast.model.view_models.home.WeatherViewModelFactory
import com.example.weatherforecast.model.view_models.setting.SettingViewModel
import com.example.weatherforecast.setIcon
import com.example.weatherforecast.view.homefragment.daily.DailyAdapter
import com.example.weatherforecast.view.homefragment.hourly.HourlyAdapter
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.weatherforecast.R


class HomeFragment : Fragment(), NetworkChangeListener {

    lateinit var binding: FragmentHomeBinding
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    // Observed settings for language and units
    private var selectedLanguage: String = "en"
    lateinit var settingViewModel: SettingViewModel
    private var selectedUnit: String = "metric"
    private var hasFetchedData = false


    private var connected = true
    lateinit var viewModel: WeatherViewModel


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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // intialize viewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = WeatherViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        // register the BroadCast here to listen for network Changes
        networkChangeReceiver = NetworkChangeReceiver(this)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireActivity().registerReceiver(networkChangeReceiver, intentFilter)

        // intialzie the fuzed
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        settingViewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)

        checkLanguageAndUnitChange()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationStatus() // and update ui
        binding.allowLocationButton.setOnClickListener { requestLocationPermissions() }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // Call a method to refresh data or recreate the fragment
            refreshFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshFragment() {
        // Show refreshing animation
        binding.swipeRefreshLayout.isRefreshing = true
        fetchWeatherData()
        loadWeatherData()

        // Stop the refreshing animation after the fragment is recreated
        binding.swipeRefreshLayout.isRefreshing = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLocationStatus() {
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                updateUI(true) // Location is enabled, show weather data
                getFreshLocation()
            } else {
                updateUI(false) // Show enable location prompt
                Toast.makeText(
                    requireContext(),
                    "Please enable location services",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Request permissions if not already granted
            //  requestLocationPermissions()
            updateUI(false)
        }

    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.My_LOCATION_PERMISSION_ID
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.My_LOCATION_PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                checkLocationStatus()
            } else {
                // Permission denied
                updateUI(false)
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        checkLocationStatus() // Check location status again
    }

    // check if the location is enabled then display the weather else display allow button
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
        if (lat == 0.0 && lon == 0.0) {
            Log.e("LocationError", "Lat/Lon are not set yet")
            return
        }
        fetchWeatherData()

        if (isAdded) {
            viewModel.currentWeather.observe(viewLifecycleOwner, Observer { weather ->
                weather?.let {
                    val decimalFormat = DecimalFormat("#.##")

                    // Set location name
                    binding.tvLocationName.text = weather.city

                    // Format and set the date
                    val dateTime = Instant.ofEpochSecond(weather.date.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                    val dateFormatter =
                        DateTimeFormatter.ofPattern("EEEE, MMMM yyyy", Locale.getDefault())
                    binding.tvCurrentDate.text = dateTime.format(dateFormatter)

                    // Set weather status and temperature
                    binding.tvWeatherStatus.text = weather.weatherStatus
                    binding.tvCurrentDegree.text =
                        decimalFormat.format(weather.temperature) + " " + getString(R.string.degree_format)

                    // Set weather icon
                    setIcon(weather.weatherIcon, binding.ivWeatherIcon)

                    // Set values for pressure, humidity, clouds, wind speed, and visibility using localized strings
                    binding.presser.text =
                        decimalFormat.format(weather.pressure) + " " + getString(R.string.unit_hpa)
                    binding.humidity.text =
                        weather.humidity.toString() + " " + getString(R.string.unit_percent)
                    binding.clouds.text =
                        weather.clouds.toString() + " " + getString(R.string.unit_percent)
                    // observe on wind Status
                    settingViewModel.windSetting.observe(requireActivity(), Observer { value ->
                        val windSpeedText = when (value) {
                            getString(R.string.mile_hour) -> {
                                // Convert wind speed from m/s to mph
                                val windSpeedMph = weather.windSpeed * 2.23694
                                "${decimalFormat.format(windSpeedMph)} ${getString(R.string.unit_miles_per_hour)}"
                            }

                            getString(R.string.meter_second) -> {
                                "${decimalFormat.format(weather.windSpeed)} ${getString(R.string.unit_meter_per_sec)}"
                            }

                            else -> "${decimalFormat.format(weather.windSpeed)} ${getString(R.string.unit_meter_per_sec)}" // Default case
                        }

                        binding.wind.text = windSpeedText
                    })
                    binding.visability.text =
                        decimalFormat.format(weather.visibility / 1000) + " " + getString(R.string.unit_kilometer)
                    binding.tvWeatherStatus.text =
                        getString(R.string.weatherState) + " ::" + weather.weatherStatus
                }
            })
        }




        hourlyAdapter = HourlyAdapter(emptyList())
        binding.hoursRecycler.apply {
            adapter = hourlyAdapter
            // Set the layout manager to horizontal
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        // try observe on hourly data
        if (isAdded) {
            viewModel.hourlyWeather.observe(viewLifecycleOwner, Observer { hourlyWeather ->
                if (hourlyWeather != null && hourlyWeather.isNotEmpty()) {
                    hourlyAdapter.submitList(hourlyWeather)

                    Log.i(Constants.SUCCESS, "HOURLY FETCHED successfuly $hourlyWeather")
                } else {
                    Log.i(Constants.ERROR, "Error fetch Hourly")

                }
            })
        }
        dailyAdapter = DailyAdapter(emptyList())
        binding.daysRecyclerView.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(context)

        }
        // observe on daily data and check it
        if (isAdded) {
            viewModel.dailyWeather.observe(viewLifecycleOwner, Observer { mapDailyWeather ->
                if (mapDailyWeather != null && mapDailyWeather.isNotEmpty()) {
                    // Drop the first item and submit the updated list
                    val listWithoutFirstItem = mapDailyWeather.drop(1)
                    dailyAdapter.submitList(listWithoutFirstItem)
                    Log.i(Constants.SUCCESS, "Daily FETCHED successfully $listWithoutFirstItem")
                } else {
                    Log.i(Constants.ERROR, "Error fetching Hourly")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private var snackbarShown = false

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData() {
        if (connected) {
            if (!hasFetchedData && lat != 0.0 && lon != 0.0) {
                snackbarShown = false // Reset the flag when connected

                // Fetch weather data
                viewModel.getCurrentWeatherRemotly(lat, lon, selectedLanguage, selectedUnit)
                viewModel.getHourlyWeatherRemotly(lat, lon, selectedLanguage, selectedUnit)
                viewModel.getDailyWeatherRemotly(lat, lon, selectedLanguage, selectedUnit)

                hasFetchedData = true // Set the flag to true after fetching
            }
        } else {
            if (!snackbarShown) {
                Snackbar.make(
                    requireView(),
                    "You see Weather Offline",
                    Snackbar.LENGTH_SHORT
                ).show()
                snackbarShown = true // Set the flag to true after showing
            }

            // Fetch local weather data
            viewModel.getCurrentWeatherLocally()
            viewModel.getHourlyWeatherLocally()
            viewModel.getDailyWeatehrLocally()
        }
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
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(5000)
            .build()
        fusedLocation.requestLocationUpdates(locationRequest, object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lat = locationResult.locations[0].latitude
                lon = locationResult.locations[0].longitude
                // Reload UI with the new location data

                if (isAdded) {
                    loadWeatherData()
                } else {
                    Log.e("HomeFragment", "Fragment not attached, skipping loadWeatherData")
                }
            }
        }, Looper.myLooper())
    }

    /*    // when comeBack to the same home fragment again
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onResume() {
            super.onResume()
            // Check again if the location is enabled
            if (checkPermissions(requireContext())) {
                if (isLocationEnabled(requireContext())) {
                    updateUI(true)
                    getFreshLocation()
                } else {
                    updateUI(false)
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
        }*/

    // implementation for network BroadCastReciver
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNetworkChanged(isConnected: Boolean) {
        connected = isConnected
        if (connected) {
            hasFetchedData = false // Reset the flag to allow fetching when reconnected
            fetchWeatherData() // Optionally fetch data when reconnected
        }
    }


    // un register the listener
    override fun onDestroy() {
        super.onDestroy()

        requireActivity().unregisterReceiver(networkChangeReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLanguageAndUnitChange() {
        settingViewModel.languageSetting.observe(requireActivity(), Observer { language ->
            selectedLanguage = language
            fetchWeatherData()
        })

        settingViewModel.unitSetting.observe(requireActivity(), Observer { unit ->
            selectedUnit = unit
            fetchWeatherData()
        })
    }

}