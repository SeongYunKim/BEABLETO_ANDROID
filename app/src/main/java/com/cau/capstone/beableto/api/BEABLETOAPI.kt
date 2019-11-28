package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.data.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.lang.StringBuilder

interface BEABLETOAPI {
    @POST("/accounts/signup/")
    fun requestSignUp(@Body body: RequestRegister): Completable

    @POST("/accounts/token/")
    fun requestLogIn(@Body body: RequestLogIn): Single<ResponseLogIn>

    @Multipart
    @POST("/information/save/")
    fun requestRegisterLocation(
        @Header("Authorization") authorization: String?,
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
    ): Single<ResponseRegisterLocation>

    @POST("/information/getmarker/")
    fun requestMarkerOnMap(
        @Header("Authorization") authorization: String?,
        @Body body: RequestMarkerOnMap
    ): Single<ResponseMarkerOnMap>

    @GET("information/getallmarkers/")
    fun requestGetAllMarkers(@Header("Authorization") authorization: String?): Single<ResponseMarkerOnMap>

    @POST("/information/getfragment/")
    fun requestFragmentOnMap(
        @Header("Authorization") authorization: String?,
        @Body body: RequestMarkerOnMap
    ): Single<ResponseFragmentOnMap>

    @POST("/information/road/")
    fun requestRegisterRoute(
        @Header("Authorization") authorization: String?,
        @Body body: RequestRegisterRoute
    ): Completable

    @POST("/information/basewalk/")
    fun requestMiddlePoint(
        @Header("Authorization") authorization: String?,
        @Body body: RequestRoute
    ): Single<ResponseMiddlePoint>

    @POST("/information/getpaths/")
    fun requestAdmin(
        @Header("Authorization") authorization: String?,
        @Body body: RequestRoute
    ): Single<ResponseAdmin>

    //TODO URL 입력
    @POST("/")
    fun requestRegisterRealTimeLocation(
        @Header("Authorization") authorization: String?,
        @Body body: RequestRegisterRealTimeLocation
    ): Completable

    @POST("/information/getlocation/")
    fun requestLocationSearch(
        @Header("Authorization") authorization: String?,
        @Body body: RequestLocationSearch
    ): Single<ResponseLocationSearch>

    @GET("/information/getrank/")
    fun requestRanking(
        @Header("Authorization") authorization: String?
    ): Single<ResponsePointRanking>

    //로그아웃 "/accounts/logout/"
}