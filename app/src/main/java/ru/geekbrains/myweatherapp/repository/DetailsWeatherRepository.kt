package ru.geekbrains.myweatherapp.repository

import ru.geekbrains.myweatherapp.domain.WeatherDTO

fun interface DetailsWeatherRepository {
    fun getWeatherDetailsFromServer(
        lat: Double,
        lon: Double,
        callback: retrofit2.Callback<WeatherDTO>
    )
}