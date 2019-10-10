package com.cau.capstone.beableto.repository

import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestRegister
import com.cau.capstone.beableto.data.ResponseRegister
import io.reactivex.Single

//private const val LOGIN_TOKEN_KEY = "logintoken"
var LOGIN_TOKEN_KEY = "logintoken"

class UserRepository() {
    init {
        LOGIN_TOKEN_KEY = "logintoken"
    }
    /*
    fun register(body: RequestRegister) : Single<ResponseRegister> {
        return NetworkCore.requestSignUp((body))
    }
    */
}