package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.data.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*
object NetworkCore {
    private val service: Service
    private val baseURL: String

    init {
        baseURL = "http://192.168.0.2:8000"

        val okHttpClient = OkHttpClient.Builder()
            .build()

        service = Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
            .create(Service::class.java)
    }

    fun requestSignUp(body: RequestRegister) : Single<ResponseRegister> {
        return service.requestSignUp(body)
    }
}
*/
object NetworkCore {
    val api: Retrofit
    val BASE_URL = "http://192.168.0.2:8000"

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