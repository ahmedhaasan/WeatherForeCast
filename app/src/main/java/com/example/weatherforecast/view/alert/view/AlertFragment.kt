package com.example.weatherforecast.view.alert.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.checkNotificationPermission
import com.example.weatherforecast.databinding.AlertDialogBinding
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.dateTimeStringToMillis
import com.example.weatherforecast.formatHourMinuteToString
import com.example.weatherforecast.formatMillisToDateTimeString
import com.example.weatherforecast.model.apistate.AlarmState
import com.example.weatherforecast.model.apistate.WeatherApiState
import com.example.weatherforecast.model.database.WeatherDataBase
import com.example.weatherforecast.model.local.LocalDataSourceImp
import com.example.weatherforecast.model.pojos.AlarmEntity
import com.example.weatherforecast.model.remote.RemoteDataSourceImp
import com.example.weatherforecast.model.reposiatory.ReposiatoryImp
import com.example.weatherforecast.model.view_models.alarm.AlarmFactory
import com.example.weatherforecast.model.view_models.alarm.AlarmViewModel
import com.example.weatherforecast.model.view_models.setting.SettingViewModel
import com.example.weatherforecast.requestNotificationPermission
import com.example.weatherforecast.setLocationNameByGeoCoder
import com.example.weatherforecast.view.alert.AlarmScheduler
import com.example.weatherforecast.view.alert.AlarmSchedulerImpl
import com.example.weatherforecast.view.alert.NotificationManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.util.Calendar

class AlertFragment : Fragment(), EasyPermissions.PermissionCallbacks { // important note


    lateinit var settingViewModel: SettingViewModel
    lateinit var alarmViewModel: AlarmViewModel

    lateinit var binding: FragmentAlertBinding
    private lateinit var dialogAlertBinding: AlertDialogBinding
    private lateinit var alarmAdapter: AlarmAdapter  // alert adapter

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var currentZoneName = ""

    /**
     *      instance from alarm Schedular to use in create and calcel alarm
     */
    lateinit var alarmScheduler: AlarmScheduler

    var isNotificationEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NotificationManager.createNotificationChannel(requireContext())  // notification Channerl
        // Initialize the alert adapter
        alarmScheduler = AlarmSchedulerImpl.getInstance(requireActivity().application)

        alarmAdapter = AlarmAdapter() { alarm ->

            deleteTheAlarm(alarm)
        }
        binding.alertRecycler.adapter = alarmAdapter
        binding.alertRecycler.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // creating instance form alarmViewModel
        val db = WeatherDataBase.getInstance(requireContext())
        val dao = db.getWeatherDao()
        val repo = ReposiatoryImp(RemoteDataSourceImp(), LocalDataSourceImp(dao))
        val factory = AlarmFactory(repo)
        // now view Model
        alarmViewModel = ViewModelProvider(
            requireActivity(),
            factory
        ).get(AlarmViewModel::class.java) // take care her to listen to alarms in db
        settingViewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)


        //  step 1 listen if notification is Enabled
        settingViewModel.notificationSetting.observe(
            viewLifecycleOwner,
            Observer { notificationStatus ->
                val value = if (notificationStatus == Constants.ENABLED) true else false
                this.isNotificationEnabled = value  // check if the notification is enabled or not

            })

        setListeners() // listen when the add button is pressed

        alarmViewModel.getAllAlarmsLocally()  // observe on data befor access it
        alarmViewModel.getCurrentWeatherLocally()


        binding.alertRecycler
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.currentWeatherState.collect() {
                    if (it is WeatherApiState.Success) {
                        currentLatitude = it.currentWeather.lat
                        currentLongitude = it.currentWeather.lon
                        currentZoneName = it.currentWeather.date.toString()
                        currentZoneName =
                            setLocationNameByGeoCoder(it.currentWeather, requireContext())
                    }
                }
            }
        }

        //

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.alarmsStateFlow.collectLatest{ results ->

                    when (results) {
                        is AlarmState.Success -> {
                            binding.alarmProgressBar.visibility = View.GONE
                            binding.alertRecycler.visibility = View.VISIBLE
                            binding.lvNoAlarms.visibility = View.GONE
                            alarmAdapter.submitList(results.alarms)
                            Log.d("Fragment", "Alarms received: ${results.alarms}")

                        }

                        is AlarmState.Failure -> {
                            Toast.makeText(
                                context,
                                "error got alarms : ${results.msg}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is AlarmState.Loading  -> {
                            binding.alarmProgressBar.visibility = View.VISIBLE

                        }
                        is AlarmState.Empty  -> {
                            binding.alarmProgressBar.visibility = View.GONE
                            binding.alertRecycler.visibility = View.GONE
                            binding.lvNoAlarms.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }


    // here if notification is not enabled from setting will ask user to enable it frist
    private fun setListeners() {
        binding.addAlarmButton.setOnClickListener {
            Log.d("Notification", "setListeners: ${isNotificationEnabled}")
            if (isNotificationEnabled) {
                if (checkNotificationPermission(requireContext())) {
                    showWeatherAlertDialog()
                } else {
                    requestNotificationPermission(requireActivity())
                }
            } else {
                Toast.makeText(
                    context,
                    "You need to enable notifications from settings first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun showWeatherAlertDialog() {

        val currentTimeInMillis = System.currentTimeMillis()

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialogAlertBinding = AlertDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogAlertBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogAlertBinding.tvFromDateDialog.text =
            formatMillisToDateTimeString(currentTimeInMillis, "dd MMM yyyy")
        dialogAlertBinding.tvFromTimeDialog.text =
            formatMillisToDateTimeString(currentTimeInMillis + 60 * 1000, "hh:mm a")
        dialogAlertBinding.cvAlarmTime.setOnClickListener {
            showDatePicker()  // when press to select day and time
        }

        dialogAlertBinding.btnSaveDialog.setOnClickListener {

            val time = dateTimeStringToMillis(  // take the user time he put
                dialogAlertBinding.tvFromDateDialog.text.toString(),
                dialogAlertBinding.tvFromTimeDialog.text.toString()
            )

            if (time > currentTimeInMillis) { // when time greater than current time
                val kindId = dialogAlertBinding.radioGroupAlertDialog.checkedRadioButtonId
                val kind =
                    if (kindId == dialogAlertBinding.radioAlert.id)
                        Constants.ALERT
                    else Constants.NOTIFICATION

                val weatherAlarm =   // create an instance of the alarm with this data
                    AlarmEntity(
                        time = time,
                        type = kind,
                        latitude = currentLatitude,
                        longitude = currentLongitude,
                        zoneName = currentZoneName
                    )

                if (kind == Constants.ALERT && !Settings.canDrawOverlays(requireContext())) {
                    requestOverlayPermission()
                    return@setOnClickListener
                }

                alarmViewModel.insertAlarmLocally(weatherAlarm)
                alarmScheduler.create(weatherAlarm) // create an alarm

                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "please select time in the future",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    // dataPicker Dialog
    private fun showDatePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText(getString(R.string.selectDate))
                .build()

        datePicker.show(parentFragmentManager, getString(R.string.date))

        datePicker.addOnPositiveButtonClickListener { date ->
            dialogAlertBinding.tvFromDateDialog.text =
                formatMillisToDateTimeString(date, "dd MMM yyyy")
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentHour)
                .setMinute(currentMinute + 1)
                .setTitleText(R.string.selectAppoinmentTime)
                .build()

        timePicker.show(parentFragmentManager, getString(R.string.time))
        timePicker.addOnPositiveButtonClickListener {
            dialogAlertBinding.tvFromTimeDialog.text =
                formatHourMinuteToString(timePicker.hour, timePicker.minute)
        }

        timePicker.addOnCancelListener {
            dialogAlertBinding.tvFromTimeDialog.text =
                formatHourMinuteToString(timePicker.hour, timePicker.minute)
        }
    }


    /**
     *      // permission to overlay  on apps while it runs and the rest of permissions
     */
    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${requireContext().packageName}")
        startActivityForResult(intent, Constants.REQUEST_OVERLAY_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == Constants.REQUEST_NOTIFICATION_PERMISSION_CODE) {
            showWeatherAlertDialog()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == Constants.REQUEST_NOTIFICATION_PERMISSION_CODE) {
            Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun deleteTheAlarm(alarm: AlarmEntity) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.deleteFavoriteDialog))
            setMessage("${getString(R.string.areYouSureDeleteLocation)}${alarm.zoneName} :${getString(R.string.alarm)} ?")
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                // If user confirms, delete the location
                alarmViewModel.deleteAlarmLocally(alarm) // now deleted
                alarmScheduler.cancel(alarm)
                Snackbar.make(
                    requireView(),
                    "Deleted Location ${alarm.zoneName}",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Undo") {
                        alarmViewModel.insertAlarmLocally(alarm)
                        alarmScheduler.create(alarm)
                    }
                    show()
                }
            }
            setNegativeButton("No", null) // Just dismiss the dialog
            create().show() // Show the dialog
        }

    }


}