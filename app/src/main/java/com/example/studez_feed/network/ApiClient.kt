package com.example.studez_feed.network



import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://prime-squirrel-inherently.ngrok-free.app/api/" // 10.0.2.2 works for localhost in Android Emulator

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}