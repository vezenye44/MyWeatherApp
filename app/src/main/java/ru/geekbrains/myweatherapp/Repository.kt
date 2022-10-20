package ru.geekbrains.myweatherapp

interface Repository {
    fun getWeatherList(): List<Weather>
    fun getWeather( lat: Double, lon: Double): Weather
}