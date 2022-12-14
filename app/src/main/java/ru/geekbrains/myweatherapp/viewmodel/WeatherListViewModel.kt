package ru.geekbrains.myweatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweatherapp.repository.ListWeatherRepository
import ru.geekbrains.myweatherapp.repository.ListWeatherRepositoryImpl
import ru.geekbrains.myweatherapp.model.Location

class WeatherListViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData<AppState>(),
    private var listWeatherRepository: ListWeatherRepository = ListWeatherRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveData

    fun getListWeatherForRussia() = getListWeather(Location.Russia)
    fun getListWeatherForWorld() = getListWeather(Location.World)
    private fun getListWeather(location: Location) {
        liveData.value = AppState.Loading
        liveData.value = AppState.SuccessListWeather(listWeatherRepository.getWeatherList(location))
    }
}
