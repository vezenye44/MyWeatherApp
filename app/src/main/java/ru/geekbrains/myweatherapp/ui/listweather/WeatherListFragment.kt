package ru.geekbrains.myweatherapp.ui.listweather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.geekbrains.myweatherapp.R
import ru.geekbrains.myweatherapp.databinding.WeatherListFragmentBinding
import ru.geekbrains.myweatherapp.ui.detailweather.DetailWeatherFragment
import ru.geekbrains.myweatherapp.util.showSnackBar
import ru.geekbrains.myweatherapp.viewmodel.AppState
import ru.geekbrains.myweatherapp.viewmodel.WeatherListViewModel

private const val IS_RUSSIA_KEY = "LIST_OF_TOWNS_KEY"
class WeatherListFragment : Fragment(){

    companion object {
        fun newInstance() = WeatherListFragment()
    }

    private lateinit var viewModel: WeatherListViewModel
    private var _binding: WeatherListFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WeatherListFragmentBinding.inflate(inflater)
        return binding.root
    }

    private var isRussia = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherListViewModel::class.java].also {model ->
            model.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        }


        showListOfTowns()

        binding.weatherFragmentFAB.setOnClickListener {
            sentList()
        }
    }

    private fun showListOfTowns() {
        activity?.let {
            if (it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_RUSSIA_KEY, true)) {
                viewModel.getListWeatherForRussia()
            } else {
                sentList()
            }
        }
    }
    private fun saveListOfTowns(isRussia: Boolean) {
        activity?.let {
            with(it.getPreferences(Context.MODE_PRIVATE).edit()) {
                putBoolean(IS_RUSSIA_KEY, isRussia)
                apply()
            }
        }
    }

    private fun sentList() { //TODO: ???????????????????? ????????????????
        if (isRussia) {
            viewModel.getListWeatherForWorld()
            binding.weatherFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getListWeatherForRussia()
            binding.weatherFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
        isRussia = !isRussia

        saveListOfTowns(isRussia)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.weatherFragmentLoadingLayout.visibility = View.GONE
                //TODO : ?????????????????? ??????????..
            }
            is AppState.Loading -> {
                binding.weatherFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.weatherFragmentLoadingLayout.visibility = View.GONE
                binding.root.showSnackBar("???????????? ????????????????", "??????????????????", {
                    if (isRussia) viewModel.getListWeatherForRussia() else viewModel.getListWeatherForWorld()
                })
            }
            is AppState.SuccessListWeather -> {
                val listWeather = appState.weatherList
                binding.weatherFragmentLoadingLayout.visibility = View.GONE
                binding.weatherFragmentRecyclerView.adapter = WeatherListAdapter(listWeather) {weather ->
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .hide(this)
                        .add(R.id.container
                            ,DetailWeatherFragment.newInstance(Bundle().apply { putParcelable(DetailWeatherFragment.BUNDLE_WEATHER, weather) }))
                        .addToBackStack("").commit()
                }
            }
        }
    }
}