package com.example.weatherforecast.view.homefragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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
import com.example.weatherforecast.model.apistate.DailyApiState
import com.example.weatherforecast.model.apistate.HourlyApiState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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
    private var lastLat = 0.0
    private var lastLon = 0.0
    private var lastLanguage = ""
    private var lastUnit = ""
    var locationStatus: String = "Map"

    private lateinit var sharedPreferences: SharedPreferences

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


    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        locationStatus = sharedPreferences.getString(Constants.LOCATIONPREFRENCES, getString(R.string.map)).toString()

        setUpAdapters()
        checkLocationStatus() // and update ui
        binding.allowLocationButton.setOnClickListener { requestLocationPermissions() }
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshFragment()
            binding.swipeRefreshLayout.isRefreshing = false // Stop the animation
        }
        checkLanguageAndUnitChange()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshFragment() {
        fetchWeatherData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLocationStatus() {
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                updateUI(true) // Location is enabled, show weather data
                if (locationStatus == Constants.MAP) {
                    // listen for any change Gps or Map location
                    getSentMapLocation()
                } else
                    getFreshLocation()
            } else {
                updateUI(false) // Show enable location prompt
                enableLocationServices()
                Toast.makeText(
                    requireContext(),
                    "Please enable location services",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Request permissions if not already granted
            updateUI(false)
            //requestLocationPermissions()
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
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permission granted for all
                updateUI(true)
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

    fun observeCurrently() {
        // Observe on current weather state
        if (isAdded) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.currentWeatherState.collectLatest { current ->
                    when (current) {
                        is WeatherApiState.Loading -> {
                            binding.homeprogressBar.visibility = View.VISIBLE
                            binding.homeScreenView.visibility = View.GONE
                        }

                        is WeatherApiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load data: ${current.msg.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is WeatherApiState.Success -> {
                            binding.homeScreenView.visibility = View.VISIBLE
                            binding.homeprogressBar.visibility = View.GONE
                            setUpCurrentView(current.currentWeather) // give the weather to current

                        }
                    }
                }
            }
        }
    }

    fun observHourly() {
        if (isAdded) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.hourlyWeatherState.collectLatest { hourly ->
                    when (hourly) {
                        is HourlyApiState.Success -> {
                            hourlyAdapter.submitList(hourly.hourlyWeather)
                        }

                        is HourlyApiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load Hourly: ${hourly.msg.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is HourlyApiState.Loading -> {

                        }
                    }
                }
            }
        }
    }

    fun observDaily() {
        // Observe on daily weather state
        if (isAdded) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.dailyWeatherState.collectLatest { daily ->
                    when (daily) {
                        is DailyApiState.Success -> {
                            val updated = daily.dailyWeatehr.drop(1)
                            dailyAdapter.submitList(updated)
                        }
                        is DailyApiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load daily weather: ${daily.msg.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is DailyApiState.Loading -> {
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpCurrentView(weather: CurrentWeatherEntity) {


        val decimalFormat = DecimalFormat("#.##")
        binding.tvLocationName.text = weather.city
        val dateTime = Instant.ofEpochSecond(weather.date.toLong())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM yyyy", Locale.getDefault())
        binding.tvCurrentDate.text = dateTime.format(dateFormatter)

        binding.tvWeatherStatus.text = weather.weatherStatus
        binding.tvCurrentDegree.text =
            decimalFormat.format(weather.temperature) + " " + getString(R.string.degree_format)

        setIcon(weather.weatherIcon, binding.ivWeatherIcon)

        binding.presser.text =
            decimalFormat.format(weather.pressure) + " " + getString(R.string.unit_hpa)
        binding.humidity.text =
            weather.humidity.toString() + " " + getString(R.string.unit_percent)
        binding.clouds.text =
            weather.clouds.toString() + " " + getString(R.string.unit_percent)
        // Observe windSetting LiveData
        settingViewModel.windSetting.observe(viewLifecycleOwner, Observer { status ->
            if (status == Constants.METER_SECOND) {
                binding.wind.text =
                    decimalFormat.format(weather.windSpeed) + " " + getString(R.string.meter_second)
            } else if(status == Constants.MILE_HOUR) {
                binding.wind.text =
                    decimalFormat.format(weather.windSpeed) + " " + getString(R.string.mile_hour)
            }
        })
        binding.visability.text =
            decimalFormat.format(weather.visibility / 1000) + " " + getString(R.string.unit_kilometer)
        binding.tvWeatherStatus.text =
            getString(R.string.weatherState) + " ::" + weather.weatherStatus

    }

    fun setUpAdapters() {
        hourlyAdapter = HourlyAdapter(emptyList())
        binding.hoursRecycler.apply {
            adapter = hourlyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        dailyAdapter = DailyAdapter(emptyList())
        binding.daysRecyclerView.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private var snackbarShown = false

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData() {
        if (connected) {
            observeCurrently()
            observDaily()
            observHourly()
            if (!hasFetchedData || lat != lastLat || lon != lastLon || selectedLanguage != lastLanguage || selectedUnit != lastUnit) {
                lastLat = lat
                lastLon = lon
                lastLanguage = selectedLanguage
                lastUnit = selectedUnit
                snackbarShown = false // Reset the flag when connected
                viewModel.getCurrentWeatherRemotly(lat, lon, selectedLanguage, selectedUnit)
                viewModel.getHourlyWeatherRemotly(lat, lon, selectedLanguage, selectedUnit)
                viewModel.getDailyWeatherRemotly(lat, lon, selectedLanguage, selectedUnit)
                hasFetchedData = true
                binding.swipeRefreshLayout.isRefreshing = false


            }
        } else {
            if (!snackbarShown) {
                Snackbar.make(requireView(), "You see Weather Offline", Snackbar.LENGTH_SHORT)
                    .show()
                snackbarShown = true
            }

            viewModel.getCurrentWeatherLocally()
            viewModel.getHourlyWeatherLocally()
            viewModel.getDailyWeatehrLocally()
            observDaily()
            observHourly()
            observeCurrently()


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

        val locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lat = locationResult.locations[0].latitude
                lon = locationResult.locations[0].longitude
                fetchWeatherData()  // after i got the lat and long then load the weather
                // Stop location updates to avoid multiple triggers
                fusedLocation.removeLocationUpdates(this)
            }
        }
        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }


    // implementation for network BroadCastReciver
    @RequiresApi(Build.VERSION_CODES.O)
    private var snackbarOnlineShown = false // For online mode


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNetworkChanged(isConnected: Boolean) {
        connected = isConnected
        if (view != null) { // Check if the view is available
            if (connected && !snackbarOnlineShown) {
                Snackbar.make(requireView(), "You are now online", Snackbar.LENGTH_SHORT).show()
                snackbarOnlineShown = true
                snackbarShown = false // Reset offline Snackbar
            } else if (!connected && !snackbarShown) {
                Snackbar.make(requireView(), "You are offline", Snackbar.LENGTH_SHORT).show()
                snackbarShown = true
                snackbarOnlineShown = false // Reset online Snackbar
            }
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
        })
        settingViewModel.unitSetting.observe(requireActivity(), Observer { unit ->
            selectedUnit = unit
        })
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSentMapLocation() {

        val latitudeStr = sharedPreferences.getString(Constants.LATITUTE, "0.0")!!.toDouble()
        val longitudeStr =sharedPreferences.getString(Constants.LONGITUTE, "0.0")?.toDouble()!!
            if (latitudeStr != null && longitudeStr != null) {
                 lat = latitudeStr
                 lon = longitudeStr
                // Use latitude and longitude
            } else {
                // Handle the case where latitude or longitude is null
                Log.e("HomeFragment", "Latitude or Longitude is null")
            }
        fetchWeatherData()

    }

}