package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class ResponseLogIn (
    //@SerializedName("code")
    //var code: Int,
    @SerializedName("key")
    var token: String
    //@SerializedName("name")
    //var name: String
)
