package ru.geekbrains.myweatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.app.App.Companion.getHistoryDao
import ru.geekbrains.myweatherapp.domain.WeatherDTO
import ru.geekbrains.myweatherapp.repository.*
import ru.geekbrains.myweatherapp.util.convertDtoToModel

private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

class DetailsViewModel(
    val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: DetailsWeatherRepository = DetailsWeatherRepositoryImpl(RemoteDataSource()),
    private val historyRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao())
) : ViewModel() {


    fun getWeatherFromRemoteSource(lat: Double, lon: Double) {
        liveData.value = AppState.Loading
        repository.getWeatherDetailsFromServer(lat, lon, callBack)
    }

    fun saveCityToDB(weather: Weather) {
        historyRepository.saveEntity(weather)
    }

    private val callBack = object : Callback<WeatherDTO> {
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            val serverResponse: WeatherDTO? = response.body()
            liveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )

        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            liveData.postValue(
                AppState.Error(
                    Throwable(
                        t.message ?: REQUEST_ERROR
                    )
                )
            )
        }

    }

    private fun checkResponse(serverResponse: WeatherDTO): AppState {
        val fact = serverResponse.fact
        return if (fact?.temp == null || fact.feels_like ==
            null || fact.condition.isNullOrEmpty()
        ) {
            AppState.Error(Throwable(CORRUPTED_DATA))
        } else {
            AppState.SuccessListWeather(convertDtoToModel(serverResponse))
        }
    }
}