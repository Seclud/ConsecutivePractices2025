package com.example.consecutivepractice.Network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.consecutivepractice.api.GamesApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.rawg.io/api/"
    private const val API_KEY = "fb6ca962884743498800663a38c928e9"

    fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url

                val url = originalUrl.newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()

                val request = original.newBuilder()
                    .url(url)
                    .build()

                chain.proceed(request)
            })
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(context))
            .build()
    }

    fun getGamesApi(context: Context): GamesApi {
        return getRetrofit(context).create(GamesApi::class.java)
    }
}