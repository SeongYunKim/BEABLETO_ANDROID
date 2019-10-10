package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.data.RequestRegister
import com.cau.capstone.beableto.data.ResponseRegister
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {

    @POST("/user")
    fun requestSignUp(@Body body: RequestRegister) : Single<ResponseRegister>
}