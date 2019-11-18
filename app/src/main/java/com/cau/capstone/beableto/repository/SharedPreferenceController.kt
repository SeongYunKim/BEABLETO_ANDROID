package com.cau.capstone.beableto.repository

import android.content.Context
import com.cau.capstone.beableto.data.Setting
import org.json.JSONArray
import org.json.JSONException

object SharedPreferenceController {

    private val AUTHORIZATION: String = "AUTHORIZATION"
    private val SETTING: String = "SETTING"
    private val REALTIME_GPS: String = "REALTIME_GPS"
    private val RECENT_SEARCH: String = "RECENT_SEARCH"
    private val KEY_TOKEN: String = "KEY_TOKEN"
    private val KEY_IS_LOGIN: String = "KEY_IS_LOGIN"
    private val KEY_SHOW_STAIR: String = "KEY_SHOW_STAIR"
    private val KEY_SHOW_SHARP: String = "KEY_SHOW_SHARP"
    private val KEY_SHOW_GENTLE: String = "KEY_SHOW_GENTLE"
    private val KEY_SHOW_ROUTE: String = "KEY_SHOW_ROUTE"
    private val KEY_REALTIME_GPS: String = "KEY_REALTIME_GPS"
    private val KEY_RECENT_WORD: String = "KEY_RECENT_WORD"
    private val KEY_RECENT_JSON: String = "KEY_RECENT_JSON"
    private var token: String? = ""
    private var isLogin: Boolean = false

    fun setAuthorization(context: Context, authorization: String?) {
        val pref = context.getSharedPreferences(AUTHORIZATION, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(KEY_TOKEN, "Token " + authorization)
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.commit()
        token = "Token " + authorization
        isLogin = true
    }

    fun getAuthorization(context: Context): String? {
        val pref = context.getSharedPreferences(AUTHORIZATION, Context.MODE_PRIVATE)
        val authorization: String? = pref.getString(KEY_TOKEN, "")
        return authorization
    }

    fun getIsLogin(context: Context): Boolean {
        val pref = context.getSharedPreferences(AUTHORIZATION, Context.MODE_PRIVATE)
        val isLogin: Boolean = pref.getBoolean(KEY_IS_LOGIN, false)
        return isLogin
    }

    fun setSetting(
        context: Context,
        stair: Boolean,
        sharp: Boolean,
        gentle: Boolean,
        route: Boolean
    ) {
        val pref = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        val editor = pref.edit()
        editor.putBoolean(KEY_SHOW_STAIR, stair)
        editor.putBoolean(KEY_SHOW_SHARP, sharp)
        editor.putBoolean(KEY_SHOW_GENTLE, gentle)
        editor.putBoolean(KEY_SHOW_ROUTE, route)
        editor.commit()
    }

    fun getSetting(context: Context): Setting {
        val pref = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
        val stair: Boolean = pref.getBoolean(KEY_SHOW_STAIR, true)
        val sharp: Boolean = pref.getBoolean(KEY_SHOW_SHARP, true)
        val gentle: Boolean = pref.getBoolean(KEY_SHOW_GENTLE, true)
        val route: Boolean = pref.getBoolean(KEY_SHOW_ROUTE, true)
        var setting = Setting(stair, sharp, gentle, route)
        return setting
    }

    fun setRealTimeGps(context: Context, real_time_gps: Boolean) {
        val pref = context.getSharedPreferences(REALTIME_GPS, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        val editor = pref.edit()
        editor.putBoolean(KEY_REALTIME_GPS, real_time_gps)
        editor.commit()
    }

    fun getRealTimeGps(context: Context): Boolean {
        val pref = context.getSharedPreferences(REALTIME_GPS, Context.MODE_PRIVATE)
        val real_time_gps = pref.getBoolean(KEY_REALTIME_GPS, true)
        return real_time_gps
    }

    fun setRecentSearchList(context: Context, search_keyword: String) {
        val pref = context.getSharedPreferences(RECENT_SEARCH, Context.MODE_PRIVATE)
        val json = pref.getString(KEY_RECENT_WORD, null)
        var tempString: String?
        val new_stringArray: ArrayList<String> = ArrayList()
        new_stringArray.add(search_keyword)
        try {
            var jsonArray = JSONArray()
            if (json != null)
                jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                tempString = jsonArray.optString(i)
                if (tempString != search_keyword) {
                    new_stringArray.add(tempString)
                    if (new_stringArray.size == 5) {
                        break
                    }
                }
            }
            val new_jsonArray = JSONArray()
            for (word in new_stringArray) {
                new_jsonArray.put(word)
            }
            if (new_stringArray.isNotEmpty()) {
                val editor = pref.edit()
                editor.clear().apply()
                editor.putString(KEY_RECENT_WORD, new_jsonArray.toString())
                editor.commit()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun getRecentSearchList(context: Context): ArrayList<String> {
        val pref = context.getSharedPreferences(RECENT_SEARCH, Context.MODE_PRIVATE)
        val json = pref.getString(KEY_RECENT_WORD, null)
        val stringArray: ArrayList<String> = ArrayList()
        if (json != null) {
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    stringArray.add(jsonArray.optString(i))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return stringArray
    }

    fun logout(context: Context) {
        val pref = context.getSharedPreferences(AUTHORIZATION, Context.MODE_PRIVATE)
        val pref2 = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
        pref2.edit().clear().apply()
        token = null
        isLogin = false
    }
}