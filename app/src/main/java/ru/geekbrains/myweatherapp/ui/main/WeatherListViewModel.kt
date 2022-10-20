package ru.geekbrains.myweatherapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherListViewModel(private val liveData: MutableLiveData<Any> = MutableLiveData<Any>()) : ViewModel() {
    fun getLiveData() = liveData

}