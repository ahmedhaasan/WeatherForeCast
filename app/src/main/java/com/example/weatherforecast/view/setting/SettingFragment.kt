package com.example.weatherforecast.view.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.Constants
import com.example.weatherforecast.LocaleHelper.setLocale
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingBinding
import com.example.weatherforecast.model.view_models.setting.SettingViewModel
import java.util.Locale

class SettingFragment : Fragment() {

    lateinit var settingViewModel: SettingViewModel
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingViewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)

        // Initialize RadioGroups based on saved preferences
        initializeRadioGroups()

        setupListeners()

    }

    private fun initializeRadioGroups() {
        // Set Location RadioGroup
        when (settingViewModel.locationSetting.value) {
            Constants.GPS -> binding.rbGps.isChecked = true
            Constants.MAP -> binding.rbMap.isChecked = true
        }

        // Set Wind Speed RadioGroup
        when (settingViewModel.windSetting.value) {

            Constants.METER_SECOND -> binding.rbMeterSecond.isChecked = true
            Constants.MILE_HOUR -> binding.rbMileHour.isChecked = true
        }

        // Set Language RadioGroup
        when (settingViewModel.languageSetting.value) {
            Constants.ENGLISH -> binding.rbEnglish.isChecked = true
            Constants.ARABIC -> binding.rbArabic.isChecked = true
        }

        // Set Notification RadioGroup
        when (settingViewModel.notificationSetting.value) {
            Constants.ENABLED -> binding.rbEnable.isChecked = true
            Constants.DISABLED -> binding.rbDisable.isChecked = true
        }

        // Set Temperature RadioGroup
        when (settingViewModel.unitSetting.value) {
            Constants.CELSIUS -> binding.rbCelsius.isChecked = true
            Constants.KELVIN -> binding.rbKelvin.isChecked = true
            Constants.FAHRENHEIT -> binding.rbFahrenheit.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.rgLocation.setOnCheckedChangeListener { _, checkedId ->
            val location = when (checkedId) {
                R.id.rb_gps -> {
                    settingViewModel.saveLocationFlag("1")
                    settingViewModel.saveLocationPreference(Constants.GPS)
                    Constants.GPS
                }

                R.id.rb_map -> {
                    val selectedLocation = Constants.MAP
                    settingViewModel.saveLocationPreference(Constants.MAP)
                    settingViewModel.locationFlag.observe(viewLifecycleOwner, Observer { flag ->
                        Log.d("SettingFragment", "locationFlag observed: $flag")
                        if ((selectedLocation == Constants.MAP) && (flag =="1")) {
                            Log.d("SettingFragment", "Navigating to MapFragment")
                            settingViewModel.saveMapCallerPrefrence(Constants.SETTINGSCREEN) // who is caller to map
                            val action = SettingFragmentDirections.actionSettingFragmentToMapFragment()
                            findNavController().navigate(action)

                            // Reset the location flag after navigating
                            settingViewModel.saveLocationFlag("0")
                        }
                    })
                    Constants.MAP
                }

                else -> ""
            }
            settingViewModel.saveLocationPreference(location)
            Toast.makeText(requireContext(), "Location set to: $location", Toast.LENGTH_SHORT).show()
        }

        // Wind Speed RadioGroup
        binding.radioGroupSettingWind.setOnCheckedChangeListener { _, checkedId ->
            val windSpeed = when (checkedId) {
                R.id.rb_meter_second -> Constants.METER_SECOND
                R.id.rb_mile_hour -> Constants.MILE_HOUR
                else -> ""

            }
            // reflect the changes in the sharedPrefrences
            settingViewModel.saveWindPreference(windSpeed)
            Toast.makeText(requireContext(), "Wind speed unit: $windSpeed", Toast.LENGTH_SHORT)
                .show()
        }

        // Language RadioGroup
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.rb_english -> {
                    settingViewModel.saveLanguagePreference(Constants.ENGLISH)
                    Constants.ENGLISH
                }

                R.id.rb_arabic -> {
                    settingViewModel.saveLanguagePreference(Constants.ARABIC)
                    Constants.ARABIC
                }
                else -> "en"
            }
            val lang = if (language == Constants.ARABIC) "ar" else "en"
            context?.let {
                setLocale(it, lang)
                // Only call recreate() if 'context' is an instance of Activity
                if (it is Activity) {
                    //findNavController().popBackStack(R.id.homeFragment, false)
                    it.recreate()
                }
            } // change the language in the all application
            Toast.makeText(requireContext(), "Language set to: $language", Toast.LENGTH_SHORT)
                .show()
        }

        // Notification RadioGroup
        binding.rgNotification.setOnCheckedChangeListener { _, checkedId ->
            val notification = when (checkedId) {
                R.id.rb_enable -> {
                    settingViewModel.saveNotificationPreference(Constants.ENABLED)
                    Constants.ENABLED
                }

                R.id.rb_disable -> {
                    settingViewModel.saveNotificationPreference(Constants.DISABLED)
                    Constants.DISABLED
                }

                else -> ""
            }
            Toast.makeText(requireContext(), "Notifications: $notification", Toast.LENGTH_SHORT)
                .show()
        }

        // Temperature RadioGroup
        binding.rgTemperature.setOnCheckedChangeListener { _, checkedId ->
            val temperatureUnit = when (checkedId) {
                R.id.rb_celsius -> {
                    settingViewModel.saveUnitPreference(Constants.CELSIUS)
                    getString(R.string.celsius)
                }

                R.id.rb_kelvin -> {
                    settingViewModel.saveUnitPreference(Constants.KELVIN)
                    getString(R.string.kelvin)
                }

                R.id.rb_fahrenheit -> {
                    settingViewModel.saveUnitPreference(Constants.FAHRENHEIT)
                    getString(R.string.fahrenheit)
                }

                else -> ""
            }
            Toast.makeText(
                requireContext(),
                "Temperature unit: $temperatureUnit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}
