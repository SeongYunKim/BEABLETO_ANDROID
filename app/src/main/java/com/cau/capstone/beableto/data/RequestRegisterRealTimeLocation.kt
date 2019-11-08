package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestRegisterRealTimeLocation (
    @SerializedName("month")
    var month: Int,
    @SerializedName("day")
    var day: Int,
    @SerializedName("hour")
    var hour: Int,
    @SerializedName("min")
    var min: Int,
    @SerializedName("latitude")
    var latitude: Float,
    @SerializedName("longitude")
    var longitude: Float
)