package com.example.FA_LoviceSunuwar_c0835390_Android;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class GetNearPlaces extends AsyncTask<Object,String,String> implements GoogleMap.OnInfoWindowClickListener {

    Context context;
    String type;
    String placeData;
    List<Address> addresses;
    String address;
    Geocoder geocoder;
    DatabaseHelper mDatabase;
    GoogleMap mMap;
    String locationUrl;
    Marker mMarker;

    public GetNearPlaces(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        locationUrl = (String) objects[1];
        type = (String)objects[2];

        GetURL getURL = new GetURL();
        try {
            placeData = getURL.readURL(locationUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //returning object of json
        return placeData;
    }
    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> naerbyplaceList = null;
        ParseData parseData = new ParseData();
        naerbyplaceList = parseData.parse(s);
        showNearByPlace(naerbyplaceList);
    }

    private void showNearByPlace(List<HashMap<String,String>> nearPlacesList) {
        for (int i = 0; i < nearPlacesList.size(); i++) {
            MarkerOptions options = new MarkerOptions();
            HashMap<String, String> mapPlace = nearPlacesList.get(i);

            final String name = mapPlace.get("placeName");
            final String vicinity = mapPlace.get("vicinity");
            final double lat = Double.parseDouble(mapPlace.get("lat"));
            final double longi = Double.parseDouble(mapPlace.get("lng"));


            LatLng latLng = new LatLng(lat, longi);
            options.position(latLng);
            options.title(name);
            options.snippet(vicinity);

            mMap.setOnInfoWindowClickListener(this);

            switch (type){

                case "school":
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    break;
                case "restaurant":
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    break;
                case "museum":
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    break;
                case "cafe":
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    break;
                case "library":
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    break;
                case "hospital":
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    break;
                default:
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    break;
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("MARKER: "+ marker.getTitle());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("You want to add this place as Favourite?");
        builder1.setCancelable(true);
        mMarker = marker;

        mDatabase = new DatabaseHelper(context);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        String addDate = simpleDateFormat.format(calendar.getTime());
                        if ( mDatabase.addFavPlace(mMarker.getTitle(), addDate, mMarker.getSnippet(), mMarker.getPosition().latitude, mMarker.getPosition().longitude)) {

                            System.out.println("printed");
                            Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
