package com.example.weatherforecast.view.favorite

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavHomeBinding
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import com.example.weatherforecast.model.view_models.home.WeatherViewModel
import com.example.weatherforecast.model.view_models.home.WeatherViewModelFactory
import com.example.weatherforecast.setIcon
import com.example.weatherforecast.view.homefragment.daily.DailyAdapter
import com.example.weatherforecast.view.homefragment.hourly.HourlyAdapter
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class Fav_Home : Fragment() {
    // declare  biew binding
    lateinit var binding: FragmentFavHomeBinding
    // some variables from home
    lateinit var fav_homeViewModel: FavoriteViewModel
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // intialize viewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = FavoriteViewModelFactory(repo)
        fav_homeViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // change the dail and current and show the data
        // Retrieve arguments
        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")

        // Make sure lat and lon are not null
        if (lat != null && lon != null) {
            fav_homeViewModel.getCurrentWeatherRemotly(lat, lon,Constants.ENGLISH, Constants.METRIC_UNIT)
            fav_homeViewModel.getDailyWeatherRemotly(lat, lon,Constants.ENGLISH, Constants.METRIC_UNIT)
            fav_homeViewModel.getHourlyWeatherRemotly(lat, lon,Constants.ENGLISH, Constants.METRIC_UNIT)
        }
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            fav_homeViewModel.currentWeather.observe(viewLifecycleOwner, Observer { weather ->
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
                    binding.wind.text =
                        decimalFormat.format(weather.windSpeed) + " " + getString(R.string.unit_meter_per_sec)
                    binding.visability.text =
                        decimalFormat.format(weather.visibility / 1000) + " " + getString(R.string.unit_kilometer)
                    binding.tvWeatherStatus.text = weather.weatherStatus+ " " +getString(R.string.weatherState)
                }
            })
        }

        hourlyAdapter = HourlyAdapter(emptyList())
        binding.hoursRecycler.apply {
            adapter = hourlyAdapter
            // Set the layout manager to horizontal
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        // try observe on hourly data
        if (isAdded) {
            fav_homeViewModel.hourlyWeather.observe(viewLifecycleOwner, Observer { hourlyWeather ->
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
            layoutManager = LinearLayoutManager(requireContext())

        }
        // observe on daily data and check it
        if (isAdded) {
            fav_homeViewModel.dailyWeather.observe(viewLifecycleOwner, Observer { mapDailyWeather ->
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
    }

