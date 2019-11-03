package com.cau.capstone.beableto.repository

import android.content.Context
import com.cau.capstone.beableto.data.Setting

object SharedPreferenceController {

    private val USER_NAME: String = "USERNAME"
    private val USER_NAME2: String = "USERNAME2"
    private val KEY1: String = "KEY1"
    private val KEY2: String = "KEY2"
    private val KEY3: String = "KEY3"
    private val KEY4: String = "KEY4"
    private val KEY5: String = "KEY5"
    private val KEY6: String = "KEY6"
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

    fun setSetting(context : Context, stair: Boolean, sharp: Boolean, gentle: Boolean, route: Boolean){
        val pref = context.getSharedPreferences(USER_NAME2, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        val editor = pref.edit()
        editor.putBoolean(KEY3, stair)
        editor.putBoolean(KEY4, sharp)
        editor.putBoolean(KEY5, gentle)
        editor.putBoolean(KEY6, route)
        editor.commit()
    }

    fun getSetting(context : Context) : Setting {
        val pref = context.getSharedPreferences(USER_NAME2, Context.MODE_PRIVATE)
        val stair :Boolean = pref.getBoolean(KEY3, true)
        val sharp :Boolean = pref.getBoolean(KEY4, true)
        val gentle :Boolean = pref.getBoolean(KEY5, true)
        val route: Boolean = pref.getBoolean(KEY6, true)
        var setting = Setting(stair, sharp, gentle, route)
        return setting
    }

    fun getIsLogin(context : Context) : Boolean {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val isLogin :Boolean = pref.getBoolean(KEY2, false)
        return isLogin
    }

    fun logout(context : Context){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val pref2 = context.getSharedPreferences(USER_NAME2, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        pref2.edit().clear().apply()
        token = null
        isLogin = false
    }
}
