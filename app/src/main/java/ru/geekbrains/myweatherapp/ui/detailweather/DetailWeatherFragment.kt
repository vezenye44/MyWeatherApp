package ru.geekbrains.myweatherapp.ui.detailweather

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import okhttp3.*
import ru.geekbrains.myweatherapp.BuildConfig
import ru.geekbrains.myweatherapp.R
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.databinding.DetailWeatherFragmentBinding
import ru.geekbrains.myweatherapp.domain.WeatherDTO
import ru.geekbrains.myweatherapp.domain.getCondition
import java.io.IOException

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
private const val MAIN_LINK = "https://api.weather.yandex.ru/v2/forecast?"

class DetailWeatherFragment : Fragment() {

    private lateinit var weatherBundle: Weather
    private var _binding: DetailWeatherFragmentBinding? = null
    private val binding get() = _binding!!

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

        val call: Call = OkHttpClient().newCall(
            Request.Builder().apply {
                addHeader("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY)
                url(MAIN_LINK + "lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
            }.build()
        ).apply {
            enqueue(object : Callback {
                val handler: Handler = Handler()

                override fun onResponse(call: Call, response: Response) {
                    val serverResponse: String? = response.body()?.string()
                    if (response.isSuccessful && serverResponse != null) {
                        handler.post {
                            displayWeather(
                                Gson().fromJson(
                                    serverResponse,
                                    WeatherDTO::class.java
                                )
                            )
                        }
                    } else {
                        TODO(PROCESS_ERROR)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    TODO(PROCESS_ERROR)
                }
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
            if (fact == null || temp == null || feelsLike == null || condition.isNullOrEmpty()
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