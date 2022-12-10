package ru.geekbrains.myweatherapp.repository

import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.domain.getRussianCities
import ru.geekbrains.myweatherapp.domain.getWorldCities
import ru.geekbrains.myweatherapp.model.Location

class ListWeatherRepositoryImpl : ListWeatherRepository {
    override fun getWeatherList(location: Location): List<Weather> = when (location) {
        Location.Russia -> getRussianCities()
        Location.World -> getWorldCities()
    }
}
