package ru.geekbrains.myweatherapp.model

import ru.geekbrains.myweatherapp.Weather

interface ListWeatherRepository {
    fun getWeatherList(location : Location): List<Weather>
}