package com.cau.capstone.beableto.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkCore {
    val api: Retrofit
    //val BASE_URL = "http://192.168.0.2:8000"
    val BASE_URL = "http://58.76.166.147:8000"

    init {
        var okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    inline fun <reified T> getNetworkCore()  = api.create(T::class.java)
}