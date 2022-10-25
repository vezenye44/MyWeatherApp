package ru.geekbrains.myweatherapp.model

import ru.geekbrains.myweatherapp.Weather

fun interface SingleWeatherRepository {
    fun getWeather( lat: Double, lon: Double): Weather
}