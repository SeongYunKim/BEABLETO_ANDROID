package com.cau.capstone.beableto.repository

import android.content.Context

object SharedPreferenceController {

    private val USER_NAME: String = "USERNAME"
    private val KEY1: String = "KEY1"
    private val KEY2: String = "KEY2"
    private var token: String? = ""
    private var isLogin: Boolean = false

    fun setAuthorization(context : Context, authorization: String?){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(KEY1, "Token " + authorization)
        editor.putBoolean(KEY2, true)
        editor.commit()
        token = "Token " + authorization
        isLogin = true
    }

    fun getAuthorization(context : Context) : String? {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val authorization :String? = pref.getString(KEY1, "")
        return authorization
    }

    fun getIsLogin(context : Context) : Boolean {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val isLogin :Boolean = pref.getBoolean(KEY2, false)
        return isLogin
    }

    fun logout(context : Context){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        token = null
        isLogin = false
    }
}
