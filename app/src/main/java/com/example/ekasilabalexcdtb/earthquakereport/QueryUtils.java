package com.example.ekasilabalexcdtb.earthquakereport;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class QueryUtils{


    public static ArrayList<Earthquake> initiateConnection(String stringUrl) {
        String jsonResponse = "";
        URL url = getURL(stringUrl);
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("EarthQuakeAsyncTask", "Error establishing Connection!!!");
        }
        ArrayList<Earthquake> earthQuakes = extractFromJson(jsonResponse);
        return earthQuakes;
    }

    public static URL getURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("EarthQuakeExtractData", "URL Exception => Not able to convert to url object.");
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("EarthQuakeExtractData", "Error response code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("EarthQuakeExtractData", "Error IOExeception");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Earthquake> extractFromJson(String jsonData) {
        ArrayList<Earthquake> earthQuakes = new ArrayList<>();
        try {
            JSONObject baseObject = new JSONObject(jsonData);
            JSONArray featuresArray = baseObject.getJSONArray("features");
            for (int i = 0; i < featuresArray.length(); i++) {
                JSONObject arrayObject = featuresArray.optJSONObject(i);
                JSONObject propertiesObject = arrayObject.optJSONObject("properties");
                earthQuakes.add(new Earthquake(propertiesObject.optDouble("mag"),
                        propertiesObject.optString("time"), propertiesObject.optLong("place"),
                        propertiesObject.optString("url")));
            }
        } catch (JSONException e) {
            Log.e("EarthQuakeExtractData", "JSON data extract error.");
        }
        return earthQuakes;
    }

}




