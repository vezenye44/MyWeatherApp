package ru.geekbrains.myweatherapp.model

sealed class Location {
    object Russia : Location()
    object World : Location()
}