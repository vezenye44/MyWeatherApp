package ru.geekbrains.myweatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweatherapp.model.ListWeatherRepository
import ru.geekbrains.myweatherapp.model.ListWeatherRepositoryImpl
import ru.geekbrains.myweatherapp.model.Location
import ru.geekbrains.myweatherapp.model.SingleWeatherRepository
import ru.geekbrains.myweatherapp.viewmodel.AppState

class WeatherListViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData<AppState>()) :
    ViewModel() {
    private var listWeatherRepository: ListWeatherRepository = ListWeatherRepositoryImpl()
    private lateinit var singleWeatherRepository: SingleWeatherRepository
    fun getLiveData() = liveData

    fun getWeather() {
        liveData.value = AppState.Loading
        liveData.value = AppState.Success(singleWeatherRepository.getWeather(0.0, 0.0))
    }

    fun getListWeatherForRussia() = getListWeather(Location.Russia)
    fun getListWeatherForWorld() = getListWeather(Location.World)
    private fun getListWeather(location: Location) {
        liveData.value = AppState.Loading
        liveData.value = AppState.SuccessListWeather(listWeatherRepository.getWeatherList(location))
    }
}
