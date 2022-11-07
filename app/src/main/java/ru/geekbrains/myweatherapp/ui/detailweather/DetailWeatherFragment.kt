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
        arguments?.getParcelable<Weather>(BUNDLE_WEATHER)?.let {weather -> renderData(weather)}
    }

    private fun renderData(weatherData: Weather) {
        with(binding) {
            this.cityName.text = weatherData.city.name
            this.cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                weatherData.city.lat.toString(),
                weatherData.city.lon.toString()
            )
            this.temperatureValue.text = weatherData.temperature.toString()
            this.feelsLikeValue.text = weatherData.feelsLike.toString()
        }
    }

    companion object {
        const val BUNDLE_WEATHER = "weather"
        fun newInstance(bundle:Bundle) = DetailWeatherFragment().apply { arguments = bundle }
    }
}