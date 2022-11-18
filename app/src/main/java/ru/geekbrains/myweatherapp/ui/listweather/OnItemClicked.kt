package ru.geekbrains.myweatherapp.ui.listweather

import ru.geekbrains.myweatherapp.Weather

fun interface OnItemClicked {
    fun onItemClick(weather: Weather)
}