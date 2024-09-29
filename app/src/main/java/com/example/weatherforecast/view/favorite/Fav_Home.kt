package com.example.weatherforecast.view.favorite

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavHomeBinding
import com.example.weatherforecast.model.apistate.DailyApiState
import com.example.weatherforecast.model.apistate.FavoriteRoomState
import com.example.weatherforecast.model.apistate.HourlyApiState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.CurrentWeatherEntity
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModel
import com.example.weatherforecast.model.view_models.favorite.FavoriteViewModelFactory
import com.example.weatherforecast.setIcon
import com.example.weatherforecast.view.homefragment.daily.DailyAdapter
import com.example.weatherforecast.view.homefragment.hourly.HourlyAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

        // Retrieve arguments
        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")

        // Make sure lat and lon are not null
        if (lat != null && lon != null) {
            fav_homeViewModel.getCurrentWeatherRemotly(
                lat,
                lon,
                Constants.ENGLISH,
                Constants.METRIC_UNIT
            )
            fav_homeViewModel.getDailyWeatherRemotly(
                lat,
                lon,
                Constants.ENGLISH,
                Constants.METRIC_UNIT
            )
            fav_homeViewModel.getHourlyWeatherRemotly(
                lat,
                lon,
                Constants.ENGLISH,
                Constants.METRIC_UNIT
            )
        }
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()

        // Observe on current weather state
        if (isAdded) {
            viewLifecycleOwner.lifecycleScope.launch {
                fav_homeViewModel.currentWeatherState.collectLatest { current ->
                    when (current) {
                        is WeatherApiState.Loading -> {
                            binding.favHomeLayout.visibility = View.GONE
                            binding.fhProgressBar.visibility = View.VISIBLE
                        }

                        is WeatherApiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load data: ${current.msg.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is WeatherApiState.Success -> {
                            binding.favHomeLayout.visibility = View.VISIBLE
                            binding.fhProgressBar.visibility = View.GONE
                            loadCurrentWeathr(current.currentWeather) // give the weather to current
                        }
                    }
                }
            }
        }

        // Observe on hourly weather state
        if (isAdded) {
            viewLifecycleOwner.lifecycleScope.launch {
                fav_homeViewModel.hourlyWeatherState.collectLatest { hourly ->
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
                            Toast.makeText(
                                requireContext(),
                                "Data is Loading",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // Observe on daily weather state
        if (isAdded) {
            viewLifecycleOwner.lifecycleScope.launch {
                fav_homeViewModel.dailyWeatherState.collectLatest { daily ->
                    when (daily) {
                        is DailyApiState.Success -> {
                            dailyAdapter.submitList(daily.dailyWeatehr)
                        }

                        is DailyApiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to load daily weather: ${daily.msg.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is DailyApiState.Loading -> {
                            Toast.makeText(
                                requireContext(),
                                "Data is Loading",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    // Functions moved outside the `if` block
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCurrentWeathr(weather: CurrentWeatherEntity) {
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
        binding.wind.text =
            decimalFormat.format(weather.windSpeed) + " " + getString(R.string.unit_meter_per_sec)
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
    }}

