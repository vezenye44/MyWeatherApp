package ru.geekbrains.myweatherapp.repository

import ru.geekbrains.myweatherapp.domain.WeatherDTO

class DetailsWeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsWeatherRepository {
    override fun getWeatherDetailsFromServer(
        lat: Double,
        lon: Double,
        callback: retrofit2.Callback<WeatherDTO>
    ) {
        remoteDataSource.getWeatherDetails(lat, lon, callback)
    }
}