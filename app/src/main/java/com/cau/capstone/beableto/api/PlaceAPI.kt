package com.cau.capstone.beableto.api

import com.cau.capstone.beableto.BuildConfig
import com.cau.capstone.beableto.data.Location

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class PlaceAPI {
    val placeAPIKey: String = BuildConfig.GOOGLE_PLACE_API_KEY
    fun autoComplete(input: String): ArrayList<String> {
        val arrayList = arrayListOf<String>()
        var connection: HttpURLConnection? = null
        val jsonResult = StringBuilder()
        try {
            val sb = StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?")
            sb.append("input=$input")
            sb.append("&language=ko&key=")
            sb.append(placeAPIKey)
            val url = URL(sb.toString())
            connection = url.openConnection() as HttpURLConnection
            val inputStreamReader = InputStreamReader(connection.inputStream)

            val buff = CharArray(1024)
            var read = inputStreamReader.read(buff)
            while (read != -1) {
                jsonResult.append(buff, 0, read)
                read = inputStreamReader.read(buff)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        try {
            arrayList.add(" $input")
            val termList = arrayListOf<String>()
            var term: String
            val jsonObject = JSONObject(jsonResult.toString())
            val prediction = jsonObject.getJSONArray("predictions")
            for (i in 0 until prediction.length()) {
                term = ""
                termList.clear()
                val terms = prediction.getJSONObject(i).getJSONArray("terms")
                for (j in 0 until terms.length()) {
                    if (terms.getJSONObject(j).getString("value") != "대한민국") {
                        termList.add(terms.getJSONObject(j).getString("value"))
                    }
                }
                for (k in termList.indices.reversed()) {
                    term = term + " " + termList[k]
                }
                arrayList.add(term)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return arrayList
    }

    fun search_place_list(input: String): ArrayList<Location> {
        val locationList = arrayListOf<Location>()
        val jsonResult = StringBuilder()
        object : Thread() {
            override fun run() {
                var connection: HttpURLConnection? = null
                try {
                    val sb =
                        StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?")
                    sb.append("query=$input")
                    sb.append("&language=ko&region=kr&key=")
                    sb.append(placeAPIKey)
                    val url = URL(sb.toString())
                    connection = url.openConnection() as HttpURLConnection
                    val inputStreamReader = InputStreamReader(connection.inputStream)

                    val buff = CharArray(1024)
                    var read = inputStreamReader.read(buff)
                    while (read != -1) {
                        jsonResult.append(buff, 0, read)
                        read = inputStreamReader.read(buff)
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    connection?.disconnect()
                }
                try {
                    val jsonObject = JSONObject(jsonResult.toString())
                    val results = jsonObject.getJSONArray("results")
                    val status = jsonObject.getString("status")
                    for (i in 0 until results.length()) {
                        val iterjsonObject = results.getJSONObject(i)
                        val address: String? =
                            results.getJSONObject(i).getString("formatted_address")
                        val name: String? = iterjsonObject.getString("name")
                        val place_id: String? = iterjsonObject.getString("place_id")
                        val latitude: Float?
                        val longitude: Float?
                        var rate: Float? = null
                        //ArrayList<String> typesList = new ArrayList();
                        //address = results.getJSONObject(i).getString("formatted_address")
                        //name = iterjsonObject.getString("name")
                        //place_id = iterjsonObject.getString("place_id")
                        if (iterjsonObject.has("rating"))
                            rate = java.lang.Float.parseFloat(iterjsonObject.getString("rating"))
                        latitude = java.lang.Float.parseFloat(
                            iterjsonObject.getJSONObject("geometry").getJSONObject("location").getString(
                                "lat"
                            )
                        )
                        longitude = java.lang.Float.parseFloat(
                            iterjsonObject.getJSONObject("geometry").getJSONObject("location").getString(
                                "lng"
                            )
                        )
                        //JSONArray types = iterjsonObject.getJSONArray("types");
                        //for(int j = 0; j < types.length(); j++){
                        //    typesList.add(types.getJSONObject(j));
                        //}
                        val location = Location(
                            latitude,
                            longitude, address!!, name!!, rate, place_id!!, null, null
                        )
                        locationList.add(location)
                    }
                    if (status == "ZERO_RESULTS") {
                        locationList.add(Location(1.0f, 0.0f, "", "", 0.0f, "", null, null))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }.start()
        return locationList
    }
}
