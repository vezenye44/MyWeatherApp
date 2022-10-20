package ru.geekbrains.myweatherapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweatherapp.AppState

class WeatherListViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData<AppState>()) : ViewModel() {
    fun getLiveData() = liveData

}