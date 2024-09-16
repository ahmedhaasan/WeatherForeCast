package com.example.weatherforecast.view

import WeatherResponse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.connection.RetrofitHelper
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            try {
                val result = RetrofitHelper.service.getCurrentWeather(44.34, 10.99, "metric")
                if (result.isSuccessful) {
                    val apiResult: WeatherResponse? = result.body()
                    Log.i("Result", apiResult?.main?.temp.toString() ?: "No temperature data")
                } else {
                    Log.e("Error", "Response code: ${result.code()}")
                }
            } catch (e: Exception) {
                Log.e("Error", "Exception: ${e.message}")
            }
        }
    }
}
