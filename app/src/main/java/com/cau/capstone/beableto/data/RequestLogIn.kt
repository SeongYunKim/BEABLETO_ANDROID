package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestLogIn (
    @SerializedName("user_id")
    var user_id: String,
    @SerializedName("password")
    var password: String
)