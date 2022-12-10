package ru.geekbrains.myweatherapp.repository

import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.model.Location

interface ListWeatherRepository {
    fun getWeatherList(location : Location): List<Weather>
}