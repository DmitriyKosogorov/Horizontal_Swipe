package com.example.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.*

class NumberAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    var cities = mutableListOf("Irkutsk")

    fun addCity(city: String)
    {
        if(cities.contains(city))
            Log.d("added","False")
        else {
            Log.d("added", city)
            cities.add(city)
        }
    }

    override fun getItemCount(): Int = cities.size

    suspend fun loadWeather(City:String): Array<String> {

        var resultec=arrayOf("Null","Null","Null","Null","Null","Null","NULL")
        val API_KEY = "7bc74e67ab92d8b035ce120f678a5799"
        //val City="Irkutsk"
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid="+API_KEY+"&units=metric"
        try {
            val stream = URL(weatherURL).getContent() as InputStream
            val data = Scanner(stream).nextLine()
            var obj = JSONObject(data)
            if (obj["cod"].toString() != "200") {
                Log.d("compleated", "False1")
            }

            else {
                Log.d("compleated","True")
                val main=obj.getJSONObject("main")
                val weather=obj.getJSONArray("weather").getJSONObject(0)
                val wind=obj.getJSONObject("wind")
                val temperature=main["temp"]
                val mainweather=weather["main"]
                val feels_like=main["feels_like"]
                val preassure=main["pressure"]
                val description=weather["description"]
                Log.d("temperature",temperature.toString())
                Log.d("main",mainweather.toString())
                resultec[0]=weather.toString()
                resultec[1]=wind.toString()
                resultec[2]=temperature.toString()
                resultec[3]=mainweather.toString()
                resultec[4]=feels_like.toString()
                resultec[5]=preassure.toString()
                resultec[6]=description.toString()
            }


            return resultec

        }
        catch(e:Exception)
        {
            Log.d("compleated","False2")
        }
        return resultec

    }

    override fun createFragment(position: Int): Fragment {
        val fragment = CurrentTaskFragment()
        fragment.arguments = Bundle().apply {
            GlobalScope.launch(Dispatchers.IO) {
                var data=loadWeather(cities[position])
                putString("city", cities[position])
                putString("weather", data[0])
                putString("wind", data[1])
                putString("temperature", data[2])
                putString("main_weather", data[3])
                putString("feels_like", data[4])
                putString("pressure", data[5])
                putString("description", data[6])
            }
        }
        return fragment
    }
}