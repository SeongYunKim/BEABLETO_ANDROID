package com.cau.capstone.beableto.api;

import android.util.Log;

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
}
