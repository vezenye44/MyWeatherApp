package ru.geekbrains.myweatherapp.model

import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.domain.getRussianCities
import ru.geekbrains.myweatherapp.domain.getWorldCities

class ListWeatherRepositoryImpl : ListWeatherRepository {
    override fun getWeatherList(location: Location): List<Weather> = when (location) {
        Location.Russia -> getRussianCities()
        Location.World -> getWorldCities()
    }
}
