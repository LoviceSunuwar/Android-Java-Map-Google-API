package com.example.FA_LoviceSunuwar_c0835390_Android;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    String googleDirectionsData;
    GoogleMap mMap;
    String url;
    Context context;
    String distance;
    String duration;
    LatLng latLng,l;


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];
        l = (LatLng) objects[3];

        GetURL fetchURL = new GetURL();
        try {
            googleDirectionsData = fetchURL.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }
    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> distances = null;
        ParseData distancesParser = new ParseData();
        distances = distancesParser.parseDistance(s);

        distance = distances.get("distance");
        duration = distances.get("duration");

        mMap.clear();
        // we create marker options

        MarkerOptions o2 = new MarkerOptions().position(l).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Duration : " + duration)
                .snippet("Distance : " + distance);
        mMap.addMarker(markerOptions);
        mMap.addMarker(o2);
        if (DurationAndDistance.directionRequested) {
            String[] directionsList;
            ParseData parser = new ParseData();
            directionsList = parser.parseDirections(s);
            Log.d("", "onPostExecute: " + directionsList);
            displayDirections(directionsList);
        }
    }

    private void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.BLACK)
                    .width(20)
                    .addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
        }
    }
}

