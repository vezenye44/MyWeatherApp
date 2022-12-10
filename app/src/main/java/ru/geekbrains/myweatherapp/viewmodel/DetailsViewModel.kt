package ru.geekbrains.myweatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.geekbrains.myweatherapp.domain.WeatherDTO
import ru.geekbrains.myweatherapp.model.DetailsWeatherRepository
import ru.geekbrains.myweatherapp.model.DetailsWeatherRepositoryImpl
import ru.geekbrains.myweatherapp.model.RemoteDataSource
import ru.geekbrains.myweatherapp.util.convertDtoToModel
import java.io.IOException

private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

class DetailsViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: DetailsWeatherRepository = DetailsWeatherRepositoryImpl(RemoteDataSource())
) : ViewModel() {
    fun getLiveData() = liveData

    fun getWeatherFromRemoteSource(requestLink: String) {
        liveData.value = AppState.Loading
        repository.getWeatherDetailsFromServer(requestLink, callBack)
    }

    private val callBack = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            liveData.postValue(AppState.Error(Throwable(e.message ?: REQUEST_ERROR)))
        }

        override fun onResponse(call: Call, response: Response) {
            val serverResponse: String? = response.body()?.string()
            liveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

    }

    private fun checkResponse(serverResponse: String): AppState {
        val weatherDTO: WeatherDTO = Gson().fromJson(serverResponse, WeatherDTO::class.java)
        val fact = weatherDTO.fact
        return if (fact == null || fact.temp == null || fact.feels_like == null || fact.condition.isNullOrEmpty()) {
            AppState.Error(Throwable(CORRUPTED_DATA))
        } else {
            AppState.SuccessListWeather(convertDtoToModel(weatherDTO))
        }
    }
}