package com.example.FA_LoviceSunuwar_c0835390_Android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseData {
        private HashMap<String,String> getNearByPlace(JSONObject jsonObject) {

        HashMap<String,String>  googleplace = new HashMap<>();
        //String placeName = "-NA-";
        String  vicinity = "-NA-";
        String lat = "";
        String lng = "";
        String refernce = "";

        try {
            if (!jsonObject.isNull("name")){

                placeName = jsonObject.getString("name");
            }
            if(!jsonObject.isNull("vicinity"))
            {
                vicinity = jsonObject.getString("vicinity");
            }
            lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
            refernce = jsonObject.getString("reference");

            googleplace.put("placeName",placeName);
            googleplace.put("vicinity",vicinity);
            googleplace.put("lat",lat);
            googleplace.put("lng",lng);
            googleplace.put("reference",refernce);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return googleplace;
    }

    private List<HashMap<String,String>> getNearByPlaces(JSONArray jsonArray){
        int count = jsonArray.length();

        List<HashMap<String,String>> placseList = new ArrayList<>();
        HashMap<String,String> places = null;

        for (int i = 0;i<count;i++){
            try {
                places = getNearByPlace((JSONObject) jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            placseList.add(places);
        }

        return placseList;
    }

    public List<HashMap<String,String>> parse(String jsonDta){
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject =  new JSONObject(jsonDta);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getNearByPlaces(jsonArray);
    }
    public HashMap<String, String> parseDistance(String jsonData) {
        JSONArray jsonArray = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {

            e.printStackTrace();
           }

        return getDuration(jsonArray);
    }

    private HashMap<String, String> getDuration(JSONArray googleDirectionJson) {
        HashMap<String, String> googleDirectionMap = new HashMap<>();
        String duration = "";
        String distance = "";

        try {
            duration = googleDirectionJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionMap.put("duration", duration);
            googleDirectionMap.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return googleDirectionMap;
    }
    public String[] parseDirections(String jsonData) {
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return getPaths(jsonArray);
    }

    public String getPath(JSONObject googlePathJson) {
        String polyLine = "";
        try {
            polyLine = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyLine;
    }

    public String[] getPaths(JSONArray googleStepsJson) {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for (int i=0; i<count; i++) {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }
}

