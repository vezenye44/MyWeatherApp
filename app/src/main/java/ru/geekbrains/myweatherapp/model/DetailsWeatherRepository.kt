package ru.geekbrains.myweatherapp.model

import okhttp3.Callback

fun interface DetailsWeatherRepository {
    fun getWeatherDetailsFromServer(requestLink: String, callback: Callback)
}