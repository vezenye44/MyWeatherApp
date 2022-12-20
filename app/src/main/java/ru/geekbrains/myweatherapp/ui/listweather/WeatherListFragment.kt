package ru.geekbrains.myweatherapp.ui.listweather

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.weather_list_fragment.*
import ru.geekbrains.myweatherapp.City
import ru.geekbrains.myweatherapp.R
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.databinding.WeatherListFragmentBinding
import ru.geekbrains.myweatherapp.ui.contentprovider.ContentProviderFragment.Companion.REQUEST_CODE
import ru.geekbrains.myweatherapp.ui.detailweather.DetailWeatherFragment
import ru.geekbrains.myweatherapp.util.showSnackBar
import ru.geekbrains.myweatherapp.viewmodel.AppState
import ru.geekbrains.myweatherapp.viewmodel.WeatherListViewModel
import java.io.IOException

private const val IS_RUSSIA_KEY = "LIST_OF_TOWNS_KEY"

class WeatherListFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherListFragment()
        private const val REFRESH_PERIOD = 60000L
        private const val MINIMAL_DISTANCE = 100f
    }

    private lateinit var viewModel: WeatherListViewModel
    private var _binding: WeatherListFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WeatherListFragmentBinding.inflate(inflater)
        return binding.root
    }

    private var isRussia = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherListViewModel::class.java].also { model ->
            model.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        }


        showListOfTowns()

        binding.mainFragmentFABLocation.setOnClickListener { checkPermission() }

        binding.weatherFragmentFAB.setOnClickListener {
            sentList()
        }
    }

    private fun showListOfTowns() {
        activity?.let {
            if (it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_RUSSIA_KEY, true)) {
                viewModel.getListWeatherForRussia()
            } else {
                sentList()
            }
        }
    }

    private fun saveListOfTowns(isRussia: Boolean) {
        activity?.let {
            with(it.getPreferences(Context.MODE_PRIVATE).edit()) {
                putBoolean(IS_RUSSIA_KEY, isRussia)
                apply()
            }
        }
    }

    private fun sentList() { //TODO: Нормалбное название
        if (isRussia) {
            viewModel.getListWeatherForWorld()
            binding.weatherFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getListWeatherForRussia()
            binding.weatherFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
        isRussia = !isRussia

        saveListOfTowns(isRussia)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.weatherFragmentLoadingLayout.visibility = View.GONE
                //TODO : придумать после..
            }
            is AppState.Loading -> {
                binding.weatherFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.weatherFragmentLoadingLayout.visibility = View.GONE
                binding.root.showSnackBar("Ошибка загрузки", "Повторить", {
                    if (isRussia) viewModel.getListWeatherForRussia() else viewModel.getListWeatherForWorld()
                })
            }
            is AppState.SuccessListWeather -> {
                val listWeather = appState.weatherList
                binding.weatherFragmentLoadingLayout.visibility = View.GONE
                binding.weatherFragmentRecyclerView.adapter = WeatherListAdapter(listWeather) { weather ->
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .hide(this)
                        .add(R.id.container,
                            DetailWeatherFragment.newInstance(Bundle().apply {
                                putParcelable(
                                    DetailWeatherFragment.BUNDLE_WEATHER,
                                    weather
                                )
                            })
                        )
                        .addToBackStack("").commit()
                }
            }
        }
    }

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                -> {
                    showRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_message))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access))
                { _, _ ->
                    requestPermission()
                }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        checkPermissionsResult(requestCode, grantResults)
    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0
                if ((grantResults.isNotEmpty())) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }
                    }
                    if (grantResults.size == grantedPermissions) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }
                return
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider =
                        locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            Companion.REFRESH_PERIOD,
                            Companion.MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                    } else {
                        getAddressAsync(context, location)
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            } else {
                showRationaleDialog()
            }
        }
    }

    private val onLocationListener = object : LocationListener {
        //TODO Проверить!!??
        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun getAddressAsync(
        context: Context,
        location: Location
    ) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                weatherFragmentFAB.post {
                    addresses?.get(0)?.let { showAddressDialog(it.getAddressLine(0), location) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(
        weather: Weather
    ) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container,
                    DetailWeatherFragment.newInstance(Bundle().apply {
                        putParcelable(DetailWeatherFragment.BUNDLE_WEATHER, weather)
                    })
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

}