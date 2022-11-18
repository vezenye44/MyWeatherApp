package ru.geekbrains.myweatherapp.ui.detailweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.geekbrains.myweatherapp.R
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.databinding.DetailWeatherFragmentBinding

class DetailWeatherFragment : Fragment() {

    private var _binding : DetailWeatherFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DetailWeatherFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO современное решение
        val weather = arguments?.get(BUNDLE_WEATHER) as Weather
        renderData(weather)
    }

    private fun renderData(weatherData: Weather) {
        binding.cityName.text = weatherData.city.name
        binding.cityCoordinates.text = String.format(
            getString(R.string.city_coordinates),
            weatherData.city.lat.toString(),
            weatherData.city.lon.toString()
        )
        binding.temperatureValue.text = weatherData.temperature.toString()
        binding.feelsLikeValue.text = weatherData.feelsLike.toString()
    }

    companion object {
        const val BUNDLE_WEATHER = "weather"

        fun newInstance(bundle:Bundle): DetailWeatherFragment {
            val fragment = DetailWeatherFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}