package com.example.fragment

import android.content.Intent
import android.content.res.Configuration
import com.example.fragment.CurrentTaskFragment
import com.example.fragment.FinishTaskFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL
import java.util.*
import androidx.viewpager2.widget.ViewPager2
import com.example.fragment.Weather
import org.json.JSONException
import org.json.JSONObject
import android.view.LayoutInflater
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.databinding.ActivityMainBinding
import androidx.fragment.app.DialogFragment
import android.content.pm.ActivityInfo


class MainActivity : AppCompatActivity() {
    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var fr1: Fragment
    lateinit var fr2: Fragment
    lateinit var finder:Button
    lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NumberAdapter
    private lateinit var viewPager: ViewPager2
    var cities = mutableListOf("Irkutsk","Bratsk", "Saratov", "Tumen")
    val class_weather=Weather()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val save=cities.toTypedArray()
        outState.putStringArray("saved_cities", save)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fm = supportFragmentManager
        ft = fm.beginTransaction()
        fr2 = FinishTaskFragment()
        finder = findViewById(R.id.find)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weather = Weather()
        adapter = NumberAdapter(this)
        Log.d("bundle-shmundle", "oncreate")
        if (savedInstanceState != null)
        {
            var saved=savedInstanceState!!.getStringArray("saved_cities")
            Log.d("bundle-shmundle",savedInstanceState.toString())
            if (savedInstanceState != null)
                if (savedInstanceState!!.containsKey("saved_cities")) {
                    Log.d("bundle-shmundle", "saved_cities here")
                }
                else {
                    Log.d("bundle-shmundle", "saved_cities not here")
                }
            Log.d("bundle-shmundle", saved.toString())
            cities = saved?.toMutableList() ?: cities

        }
        for(city in cities)
            adapter.addCity(city)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = adapter

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.languages)
        toolbar.setOnMenuItemClickListener { item -> setLocaleLanguage(item.toString()); true }
    }

    public fun updateview()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    suspend fun loadWeather() {

        class_weather.error=""
        val API_KEY = "7bc74e67ab92d8b035ce120f678a5799"
        //val City="Irkutsk"
        val City=binding.City.text
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid="+API_KEY+"&units=metric"
        try {
            val stream = URL(weatherURL).getContent() as InputStream
            val data = Scanner(stream).nextLine()
            var obj = JSONObject(data)
            if (obj["cod"].toString() != "200") {
                Log.d("compleated", "False1")
                class_weather.error = "Ошибка в названии города"
            }

            else {
                Log.d("compleated","True")
                adapter.addCity(City.toString())
                cities.add(City.toString())
            }
        }
        catch(e:Exception)
        {
            Log.d("compleated","False2")
            class_weather.error="Ошибка в названии города"
            binding.weather=class_weather
        }
        updateview()
    }

    fun setLocaleLanguage(localecode: String)
    {
        var coder="ru"
        when(localecode)
        {
            "Русский" -> coder="ru"
            "English" -> coder="en"
        }
        Log.d("localcode", coder)
        val locale = Locale(coder)
        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        @Suppress("DEPRECATION")
        config.locale = locale
        @Suppress("DEPRECATION")
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        var intent: Intent =Intent(this,MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    public fun upvi(v: View)
    {
        updateview()
    }
    public fun onClick(v: View) {

        GlobalScope.launch (Dispatchers.IO) {
            loadWeather()
        }
    }

}
