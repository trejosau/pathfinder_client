package com.example.pathfinder_client.data.network.service

import com.example.pathfinder_client.data.remote.api.WifiApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ESP32RetrofitClient {

    private const val BASE_URL = "http://192.168.4.1:8090/"



    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wifiApiService: WifiApiService = retrofit.create(WifiApiService::class.java)
}
