package ru.geekbrains.myweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.geekbrains.myweatherapp.ui.listweather.WeatherListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WeatherListFragment.newInstance())
                .commitNow()
        }
    }
}