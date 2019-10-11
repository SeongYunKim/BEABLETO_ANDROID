package com.cau.capstone.beableto.repository

import android.content.Context

object SharedPreferenceController {

    private val USER_NAME: String = "USERNAME"
    private val KEY: String = "KEY"
    private var token: String? = ""

    fun setAuthorization(context : Context, authorization: String?){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(KEY, authorization)
        editor.commit()
        token = authorization
    }

    fun getAuthorization(context : Context) : String? {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val authorization :String? = pref.getString(KEY, "")
        return authorization
    }

    fun logout(context : Context){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        token = null
    }
}
