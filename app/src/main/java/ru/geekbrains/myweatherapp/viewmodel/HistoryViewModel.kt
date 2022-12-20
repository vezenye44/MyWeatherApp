package ru.geekbrains.myweatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweatherapp.app.App.Companion.getHistoryDao
import ru.geekbrains.myweatherapp.repository.LocalRepository
import ru.geekbrains.myweatherapp.repository.LocalRepositoryImpl

class HistoryViewModel(
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepository: LocalRepository =
        LocalRepositoryImpl(getHistoryDao())
) : ViewModel() {
    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        historyLiveData.value =
            AppState.SuccessListWeather(historyRepository.getAllHistory())
    }
}
