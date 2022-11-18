package ru.geekbrains.myweatherapp.viewmodel

import ru.geekbrains.myweatherapp.Weather

sealed class AppState {
    data class Success(val weatherData: Weather) : AppState()
    data class SuccessListWeather(val weatherList: List<Weather>) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
