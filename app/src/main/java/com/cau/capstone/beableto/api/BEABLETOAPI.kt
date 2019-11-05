package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.data.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface BEABLETOAPI {
    @POST("/accounts/signup/")
    fun requestSignUp(@Body body: RequestRegister) : Completable

    @POST("/accounts/token/")
    fun requestLogIn(@Body body: RequestLogIn) : Single<ResponseLogIn>

    @Multipart
    @POST("/information/save/")
    fun requestRegisterLocation(@Header("Authorization") authorization: String?,
                                @Part image: MultipartBody.Part?,
                                @Part("location_name") location_name: String,
                                @Part("location_address") location_address: String,
                                @Part("x_axis") x_axis: Float,
                                @Part("y_axis") y_axis: Float,
                                @Part("slope") slope: Int,
                                @Part("auto_door") auto_door: Boolean,
                                @Part("elevator") elevator: Boolean,
                                @Part("toilet") toilet: Boolean,
                                @Part("comment") comment: String?
                                ) : Single<ResponseRegisterLocation>

    @POST("/information/getmarker/")
    fun requestMarkerOnMap(@Header("Authorization") authorization: String?,
                           @Body body: RequestMarkerOnMap) : Single<ResponseMarkerOnMap>

    @POST("/information/road/")
    fun requestRegisterRoute(@Header("Authorization") authorization: String?,
                             @Body body: RequestRegisterRoute) : Completable

    //로그아웃 "/accounts/logout/"
}