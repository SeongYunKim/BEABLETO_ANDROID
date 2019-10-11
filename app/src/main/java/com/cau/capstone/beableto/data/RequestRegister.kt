package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestRegister(
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var user_id: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("guardian_phone")
    var guardian_phone: String,
    @SerializedName("aids")
    var aids: String?,
    @SerializedName("push_agree")
    var push_agree: Boolean
)