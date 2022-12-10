package ru.geekbrains.myweatherapp.util

import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.domain.FactDTO
import ru.geekbrains.myweatherapp.domain.WeatherDTO
import ru.geekbrains.myweatherapp.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(
        Weather(
            getDefaultCity(), fact.temp!!, fact.feels_like!!,
            fact.condition!!
        )
    )
}