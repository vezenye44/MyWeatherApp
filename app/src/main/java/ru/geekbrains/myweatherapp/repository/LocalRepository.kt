package ru.geekbrains.myweatherapp.repository

import ru.geekbrains.myweatherapp.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun saveEntity(weather: Weather)
}