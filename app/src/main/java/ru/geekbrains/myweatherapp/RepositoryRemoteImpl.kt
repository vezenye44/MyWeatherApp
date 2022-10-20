package ru.geekbrains.myweatherapp

import java.io.IOException

class RepositoryRemoteImpl: Repository {
    override fun getWeatherList(): List<Weather> {
        try {
            Thread{
                Thread.sleep(200L)
            }
        }catch (_: IOException){}
        finally {
            return listOf(Weather())
        }
    }

    override fun getWeather(lat: Double, lon: Double): Weather {
        try {
            Thread{
                Thread.sleep(200L)
            }
        }catch (_: IOException){}
        finally {
            return Weather()
        }

    }
}