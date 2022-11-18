package ru.geekbrains.myweatherapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import ru.geekbrains.myweatherapp.databinding.ActivityWebBrouseBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class WebBrouseActivity : AppCompatActivity() {

    private var _binding:ActivityWebBrouseBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWebBrouseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ok.setOnClickListener(clickListener)
    }


    var clickListener: View.OnClickListener = object : View.OnClickListener {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onClick(v: View?) {
            try {
                // https://api.weather.yandex.ru/v2/informers?
                val uri = URL("https://api.weather.yandex.ru/v2/forecast?lat=53.9&lon=27.5667")
                val handler = Handler()
                Thread {
                    var urlConnection: HttpsURLConnection? = null
                    try {
                        urlConnection = uri.openConnection() as HttpsURLConnection
                        urlConnection.requestMethod = "GET"
                        urlConnection.readTimeout = 10000
                        urlConnection.addRequestProperty("X-Yandex-API-Key",BuildConfig.WEATHER_API_KEY)
                        val code = urlConnection.responseCode
                        if (code == 200) {
                            val inputStream = urlConnection.inputStream
                            val inputStreamReader = InputStreamReader(inputStream)
                            val reader = BufferedReader(inputStreamReader)
                            val result = getLines(reader)
                            handler.post {
                                binding.webview.loadDataWithBaseURL(null, result, "text/html; charset=utf-8",
                                    "utf-8", null)
                            }
                        } else {
                            val errorStream = urlConnection.errorStream
                            Log.e("", errorStream.toString())
                        }
                    } catch (e: Exception) {
                        Log.e("", "Fail connection", e)
                        e.printStackTrace()
                    } finally {
                        urlConnection?.disconnect()
                    }
                }.start()
            } catch (e: MalformedURLException) {
                Log.e("", "Fail URI", e)
                e.printStackTrace()
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun getLines(reader: BufferedReader): String {
            return reader.lines().collect(Collectors.joining("\n"))
        }

    }
}