package ru.geekbrains.myweatherapp.ui.detailweather

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.squareup.picasso.Picasso
import okhttp3.*
import ru.geekbrains.myweatherapp.City
import ru.geekbrains.myweatherapp.R
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.databinding.DetailWeatherFragmentBinding
import ru.geekbrains.myweatherapp.domain.getCondition
import ru.geekbrains.myweatherapp.util.showSnackBar
import ru.geekbrains.myweatherapp.viewmodel.AppState
import ru.geekbrains.myweatherapp.viewmodel.DetailsViewModel

private const val MAIN_LINK = "https://api.weather.yandex.ru/v2/forecast?"

class DetailWeatherFragment : Fragment() {

    private lateinit var weatherBundle: Weather
    private var _binding: DetailWeatherFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DetailWeatherFragmentBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable(BUNDLE_WEATHER) ?: Weather()
        viewModel.liveData.observe(viewLifecycleOwner) {
            renderData(it)
        }
        viewModel.getWeatherFromRemoteSource(weatherBundle.city.lat, weatherBundle.city.lon)
    }

    private fun displayWeather(weather: Weather) {
        val city = weatherBundle.city
        saveCity(city, weather)
        with(binding) {
            cityName.text = city.name
            cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                city.lat.toString(),
                city.lon.toString()
            )
            temperatureValue.text = weather.temperature.toString()
            feelsLikeValue.text = weather.feelsLike.toString()
            weatherCondition.text = getCondition(weather.condition)

            Picasso
                .get()
                .load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                .into(headerIcon)
        }
    }

    private fun saveCity(
        city: City,
        weather: Weather
    ) {
        viewModel.saveCityToDB(
            Weather(
                city,
                weather.temperature,
                weather.feelsLike,
                weather.condition
            )
        )
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.SuccessListWeather -> {
                binding.root.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                displayWeather(appState.weatherList[0])
            }
            is AppState.Loading -> {
                binding.root.visibility = View.GONE
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.root.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                binding.root.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        viewModel.getWeatherFromRemoteSource(weatherBundle.city.lat, weatherBundle.city.lon)
                    })
            }
            else -> {}
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val BUNDLE_WEATHER = "weather"
        fun newInstance(bundle: Bundle) = DetailWeatherFragment().apply { arguments = bundle }
    }
}