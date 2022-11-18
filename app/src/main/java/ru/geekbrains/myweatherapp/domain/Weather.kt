package ru.geekbrains.myweatherapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City (
    val name: String,
    val lat: Double,
    val lon: Double
) : Parcelable
@Parcelize
data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 23,
    val feelsLike: Int = 20
) : Parcelable
fun getDefaultCity() = City("Москва", 55.755826, 37.617299900000035)


