package ru.geekbrains.myweatherapp.ui.listweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.myweatherapp.Weather
import ru.geekbrains.myweatherapp.databinding.WeatherListFragmentRecyclerItemBinding

class WeatherListAdapter(private val weatherList: List<Weather>,
                         private val onItemClicked: OnItemClicked)
    : RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = WeatherListFragmentRecyclerItemBinding.inflate(LayoutInflater.from(parent.context))
        return WeatherViewHolder(binding)
    }

    override fun getItemCount() = weatherList.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    inner class WeatherViewHolder(private val binding: WeatherListFragmentRecyclerItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: Weather) {
            binding.apply {
                this.weatherFragmentRecyclerItemTextView.text = weather.city.name
                this.root.setOnClickListener { onItemClicked.onItemClick(weather) }
            }
        }
    }
}


