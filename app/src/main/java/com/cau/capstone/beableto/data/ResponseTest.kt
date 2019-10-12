package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class ResponseTest (
    @SerializedName("message")
    var phone: String
)