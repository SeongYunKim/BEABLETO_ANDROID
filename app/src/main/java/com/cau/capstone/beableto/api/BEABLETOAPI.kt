package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.data.RequestLogIn
import com.cau.capstone.beableto.data.RequestRegister
import com.cau.capstone.beableto.data.ResponseLogIn
import com.cau.capstone.beableto.data.ResponseRegister
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface BEABLETOAPI {
    @POST("/user/")
    fun requestSignUp(@Body body: RequestRegister) : Completable

    @POST("/user/login")
    fun requestLogIn(@Body body: RequestLogIn) : Single<ResponseLogIn>
}