package ru.geekbrains.myweatherapp.util

import ru.geekbrains.myweatherapp.City
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.domain.FactDTO
import ru.geekbrains.myweatherapp.domain.WeatherDTO
import ru.geekbrains.myweatherapp.getDefaultCity
import ru.geekbrains.myweatherapp.room.HistoryEntity

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(
        Weather(
            getDefaultCity(), fact.temp!!, fact.feels_like!!,
            fact.condition!!
        )
    )
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>):List<Weather> {
    return entityList.map {
        Weather(City(it.city, 0.0, 0.0), it.temperature, 0, it.condition)
    }
}
fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity(0, weather.city.name, weather.temperature, weather.condition)
}
