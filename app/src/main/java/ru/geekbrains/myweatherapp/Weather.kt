package ru.geekbrains.myweatherapp

data class City(
    val name: String,
    val lat: Double,
    val lon: Double
)

data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 23,
    val feelsLike: Int = 20
)
fun getDefaultCity() = City("Москва", 55.755826, 37.617299900000035)
