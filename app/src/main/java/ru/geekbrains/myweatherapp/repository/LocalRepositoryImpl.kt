package ru.geekbrains.myweatherapp.repository

import android.os.Handler
import android.os.HandlerThread
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.room.HistoryDao
import ru.geekbrains.myweatherapp.util.convertHistoryEntityToWeather
import ru.geekbrains.myweatherapp.util.convertWeatherToEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao) :
    LocalRepository {
    private val handlerThread = HandlerThread("databaseWorker")

    override fun getAllHistory(): List<Weather> {
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        lateinit var listWeather : List<Weather>
        handler.post() {
            listWeather = convertHistoryEntityToWeather(localDataSource.all())
        }

        return listWeather
    }
    override fun saveEntity(weather: Weather) {
        localDataSource.insert(convertWeatherToEntity(weather))
    }
}
