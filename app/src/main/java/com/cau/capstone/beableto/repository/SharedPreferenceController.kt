package com.cau.capstone.beableto.repository

import android.content.Context
import com.cau.capstone.beableto.data.Location
import com.cau.capstone.beableto.data.Setting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val KEY_RECENT_LOCATIONS: String = "KEY_RECENT_LOCATIONS"
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

    fun setRecentSearchList(context: Context, search_keyword: String, search_location: ArrayList<Location>) {
        val pref = context.getSharedPreferences(RECENT_SEARCH, Context.MODE_PRIVATE)
        val editor = pref.edit()
        val gson = Gson()
        val word_json = pref.getString(KEY_RECENT_WORD, null)
        val locations_json = pref.getString(KEY_RECENT_LOCATIONS, null)
        var temp_word_string: String
        var temp_locations_string: String
        val new_word_array: ArrayList<String> = ArrayList()
        val new_locations_array: ArrayList<String> = ArrayList()
        new_word_array.add(search_keyword)
        new_locations_array.add(gson.toJson(search_location))
        try {
            var word_json_array = JSONArray()
            var locations_json_array = JSONArray()
            if (word_json != null){
                word_json_array = JSONArray(word_json)
                locations_json_array = JSONArray(locations_json)
            }
            for (i in 0 until word_json_array.length()) {
                temp_word_string = word_json_array.optString(i)
                temp_locations_string = locations_json_array.optString(i)
                if (temp_word_string != search_keyword) {
                    new_word_array.add(temp_word_string)
                    new_locations_array.add(temp_locations_string)
                    if (new_word_array.size == 5) {
                        break
                    }
                }
            }
            val new_word_json_array = JSONArray()
            val new_locations_json_array = JSONArray()
            for (i in 0 until new_word_array.size) {
                new_word_json_array.put(new_word_array[i])
                new_locations_json_array.put(new_locations_array[i])
            }
            if (new_word_array.isNotEmpty()) {
                editor.clear().apply()
                editor.putString(KEY_RECENT_WORD, new_word_json_array.toString())
                editor.putString(KEY_RECENT_LOCATIONS, new_locations_json_array.toString())
                editor.commit()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun getRecentSearchWord(context: Context): ArrayList<String> {
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

    fun getRecentSearchLocation(context: Context, position: Int): ArrayList<Location> {
        val pref = context.getSharedPreferences(RECENT_SEARCH, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = pref.getString(KEY_RECENT_LOCATIONS, null)
        var locationArray: ArrayList<Location> = ArrayList()
        val type = object: TypeToken<ArrayList<Location>>(){}.type
        if (json != null) {
            try {
                val jsonArray = JSONArray(json)
                locationArray = gson.fromJson(jsonArray.optString(position), type)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return locationArray
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