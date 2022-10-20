package ru.geekbrains.myweatherapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweatherapp.AppState
import ru.geekbrains.myweatherapp.Repository
import ru.geekbrains.myweatherapp.RepositoryLocalImpl
import ru.geekbrains.myweatherapp.RepositoryRemoteImpl

class WeatherListViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData<AppState>()) :
    ViewModel() {
    lateinit var repository: Repository
    fun getLiveData() = liveData
    private fun chooseRepository() =
        if ((1..3).random() == 2) {
            RepositoryRemoteImpl()
        } else {
            RepositoryLocalImpl()
        }
    fun getWeather() {
        liveData.value = AppState.Loading
        repository = chooseRepository()
        liveData.value = AppState.Success(repository.getWeather(0.0,0.0))
    }
}
