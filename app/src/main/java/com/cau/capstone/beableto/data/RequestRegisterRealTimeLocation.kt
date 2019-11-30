package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestRegisterRealTimeLocation (
    @SerializedName("x")
    var latitude: Float,
    @SerializedName("y")
    var longitude: Float
)