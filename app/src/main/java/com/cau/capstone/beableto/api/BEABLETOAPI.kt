package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.data.*
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface BEABLETOAPI {
    @POST("/accounts/signup/")
    fun requestSignUp(@Body body: RequestRegister) : Completable

    @POST("/accounts/token/")
    fun requestLogIn(@Body body: RequestLogIn) : Single<ResponseLogIn>

    @GET("/qtest/hello/")
    fun test(@Header("Authorization") authorization : String?): Single<ResponseTest>

    //로그아웃 "/accounts/logout/"
}