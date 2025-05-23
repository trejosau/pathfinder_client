package com.example.pathfinder_client.data.network.service

import com.example.pathfinder_client.data.remote.api.AuthApiService
import com.example.pathfinder_client.data.remote.api.DeviceApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://3.148.112.227:6655/api/v1/"

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

    val deviceApiService: DeviceApiService = retrofit.create(DeviceApiService::class.java)
    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}