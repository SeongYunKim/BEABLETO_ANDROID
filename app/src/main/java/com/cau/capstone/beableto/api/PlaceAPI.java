package com.cau.capstone.beableto.api;

import android.os.Message;
import android.util.Log;

import com.cau.capstone.beableto.data.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceAPI {
    public ArrayList<String> autoComplete(String input) {
        ArrayList<String> arrayList = new ArrayList();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input=" + input);
            sb.append("&language=ko&key=AIzaSyDoeoufnh8GwLum7g57Q4P3LACvSYehwow");
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            arrayList.add(input);
            ArrayList<String> termList = new ArrayList();
            String term = "";
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray prediction = jsonObject.getJSONArray("predictions");
            for (int i = 0; i < prediction.length(); i++) {
                term = "";
                termList.clear();
                JSONArray terms = prediction.getJSONObject(i).getJSONArray("terms");
                for (int j = 0; j < terms.length(); j++) {
                    if (!terms.getJSONObject(j).getString("value").equals("대한민국")) {
                        termList.add(terms.getJSONObject(j).getString("value"));
                    }
                }
                for (int k = termList.size() - 1; k >= 0; k--) {
                    term = term + " " + termList.get(k);
                }
                arrayList.add(term);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public ArrayList<Location> search_place_list(String input) {
        final String finalInput = input;
        final ArrayList<Location> locationList = new ArrayList();
        final StringBuilder jsonResult = new StringBuilder();
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
                    sb.append("query=" + finalInput);
                    sb.append("&language=ko&key=AIzaSyDoeoufnh8GwLum7g57Q4P3LACvSYehwow");
                    URL url = new URL(sb.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

                    int read;
                    char[] buff = new char[1024];
                    while ((read = inputStreamReader.read(buff)) != -1) {
                        jsonResult.append(buff, 0, read);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonResult.toString());
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject iterjsonObject = results.getJSONObject(i);
                        String address = null, name = null, place_id = null;
                        Float latitude, longitude, rate;
                        //ArrayList<String> typesList = new ArrayList();
                        address = results.getJSONObject(i).getString("formatted_address");
                        name = iterjsonObject.getString("name");
                        place_id = iterjsonObject.getString("place_id");
                        rate = Float.parseFloat(iterjsonObject.getString("rating"));
                        latitude = Float.parseFloat(iterjsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                        longitude = Float.parseFloat(iterjsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                        //JSONArray types = iterjsonObject.getJSONArray("types");
                        //for(int j = 0; j < types.length(); j++){
                        //    typesList.add(types.getJSONObject(j));
                        //}
                        Location location = new Location(latitude, longitude, address, name, rate, place_id, null);
                        locationList.add(location);
                    }
                    Log.d("Test", locationList.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return locationList;
    }
}
