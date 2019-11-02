package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestRegisterRoute (
    @SerializedName("coordinate")
    val coordinate: MutableList<Coordinate>,
    @SerializedName("slope")
    val slope: Int
)