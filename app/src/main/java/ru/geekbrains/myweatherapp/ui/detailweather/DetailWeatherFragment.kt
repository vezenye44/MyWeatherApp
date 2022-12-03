package ru.geekbrains.myweatherapp.ui.detailweather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import ru.geekbrains.myweatherapp.R
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.databinding.DetailWeatherFragmentBinding
import ru.geekbrains.myweatherapp.domain.FactDTO
import ru.geekbrains.myweatherapp.domain.WeatherDTO
import ru.geekbrains.myweatherapp.domain.getCondition
import ru.geekbrains.myweatherapp.viewmodel.DetailsService
import ru.geekbrains.myweatherapp.viewmodel.LATITUDE_EXTRA
import ru.geekbrains.myweatherapp.viewmodel.LONGITUDE_EXTRA

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_TEMP_EXTRA = "TEMPERATURE"
const val DETAILS_FEELS_LIKE_EXTRA = "FEELS LIKE"
const val DETAILS_CONDITION_EXTRA = "CONDITION"
private const val TEMP_INVALID = -100
private const val FEELS_LIKE_INVALID = -100
private const val PROCESS_ERROR = "Обработка ошибки"

class DetailWeatherFragment : Fragment() {

    private lateinit var weatherBundle: Weather
    private var _binding: DetailWeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private val loadResultsReceiver: BroadcastReceiver = object :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            fun showError(error: String) {
                Toast.makeText(requireContext(), "ERROR IN : + $error", Toast.LENGTH_LONG).show()
            }
            when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_INTENT_EMPTY_EXTRA -> showError(DETAILS_INTENT_EMPTY_EXTRA)
                DETAILS_DATA_EMPTY_EXTRA -> showError(DETAILS_DATA_EMPTY_EXTRA)
                DETAILS_RESPONSE_EMPTY_EXTRA -> showError(DETAILS_RESPONSE_EMPTY_EXTRA)
                DETAILS_REQUEST_ERROR_EXTRA -> showError(DETAILS_REQUEST_ERROR_EXTRA)
                DETAILS_REQUEST_ERROR_MESSAGE_EXTRA -> showError(DETAILS_REQUEST_ERROR_MESSAGE_EXTRA)
                DETAILS_URL_MALFORMED_EXTRA -> showError(DETAILS_URL_MALFORMED_EXTRA)
                DETAILS_RESPONSE_SUCCESS_EXTRA -> displayWeather(
                    WeatherDTO(
                        FactDTO(
                            intent.getIntExtra(
                                DETAILS_TEMP_EXTRA, TEMP_INVALID
                            ),
                            intent.getIntExtra(
                                DETAILS_FEELS_LIKE_EXTRA,
                                FEELS_LIKE_INVALID
                            ),
                            intent.getStringExtra(
                                DETAILS_CONDITION_EXTRA
                            )
                        )
                    )
                )
                else -> showError("else errors")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            it.registerReceiver(
                loadResultsReceiver,
                IntentFilter(DETAILS_INTENT_FILTER)
            )
        }
    }

    override fun onDestroy() {
        context?.let {
            it.unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DetailWeatherFragmentBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable(BUNDLE_WEATHER) ?: Weather()
        getWeather()
    }

    private fun getWeather() {
        binding.root.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        context?.let {
            it.startService(Intent(it, DetailsService::class.java).apply {
                putExtra(
                    LATITUDE_EXTRA,
                    weatherBundle.city.lat
                )
                putExtra(
                    LONGITUDE_EXTRA,
                    weatherBundle.city.lon
                )
            })
        }
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            root.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
            val fact = weatherDTO.fact
            val temp = fact!!.temp
            val feelsLike = fact.feels_like
            val condition = fact.condition
            if (temp == TEMP_INVALID || feelsLike == FEELS_LIKE_INVALID || condition
                == null
            ) {
                TODO(PROCESS_ERROR)
            } else {
                val city = weatherBundle.city
                cityName.text = city.name
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    city.lat.toString(),
                    city.lon.toString()
                )
                temperatureValue.text = temp.toString()
                feelsLikeValue.text = feelsLike.toString()
                weatherCondition.text = getCondition(condition)
            }
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