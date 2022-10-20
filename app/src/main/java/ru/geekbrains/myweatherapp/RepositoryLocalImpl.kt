package ru.geekbrains.myweatherapp

class RepositoryLocalImpl: Repository {
    override fun getWeatherList(): List<Weather> {
        return listOf(Weather())
    }

    override fun getWeather(lat: Double, lon: Double): Weather {
        return Weather()
    }
}