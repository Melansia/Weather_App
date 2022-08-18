package com.example.weatherapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var tvWeather: TextView

    companion object {
        const val apiKey = BuildConfig.API_KEY
        private const val WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=$apiKey&units=metric"  // your API after APPID=
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity = findViewById(R.id.etCity)
        btnGetWeather = findViewById(R.id.btnGetWeather)
        tvWeather = findViewById(R.id.tvWeather)


    }

    fun onClickShowWeather(view: View) {
        val city = etCity.text.toString().trim()
        Log.i("Result", "$city")
        if (city.isNotEmpty()) {
            val task = DownloadWeatherTask()
            val url = String.format(WEATHER_URL, city)
            task.execute(url)
        }
    }

    private inner class DownloadWeatherTask : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: String?): String? {
            val result = StringBuilder()
            val url: URL?
            var urlConnection: HttpURLConnection? = null
            try {
                url = URL(p0[0])
                urlConnection = url.openConnection() as HttpURLConnection
                val streamInp = urlConnection.inputStream
                val reader = InputStreamReader(streamInp)
                val bufferedReader = BufferedReader(reader)
                var line = bufferedReader.readLine()
                while (line != null) {
                    result.append(line)
                    line = bufferedReader.readLine()
                }
                return result.toString()
            } catch (e: Exception) {
                e.message
            } finally {
                urlConnection?.disconnect()
            }
            return null
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                val city = jsonObject.getString("name")
                val temp = jsonObject.getJSONObject("main").getString("temp")
                val description =
                    jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
                val weather =
                    String.format("%s\nTemperature: %s\nOutside : %s", city, temp, description)
                tvWeather.text = weather
            } catch (e: Exception) {
                e.message
            }
        }
    }
}