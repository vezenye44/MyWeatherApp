package ru.geekbrains.myweatherapp.model

import okhttp3.Callback

class DetailsWeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsWeatherRepository {
    override fun getWeatherDetailsFromServer(requestLink: String, callback: Callback) {
        remoteDataSource.getWeatherDetails(requestLink, callback)
    }
}