package com.example.catapi

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.catapi.data.CatService
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var catService: CatService

    lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.iv)
        fetchData()
    }

    private fun fetchData() {
        Log.i("dsds", "fetchData called")

        lifecycleScope.launchWhenCreated {

            delay(2000)
            val response = catService.getSupportedLanguages()

            Log.i("dsds", "response: $response")

            Picasso.get().load(response[0].url).into(image);
        }
    }
}