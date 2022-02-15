package com.example.FA_LoviceSunuwar_c0835390_Android;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.FA_LoviceSunuwar_c0835390_Android.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DurationAndDistance extends AppCompatActivity implements OnMapReadyCallback, Serializable {
    GoogleMap mMap;
    double lat, longi, dest_lat, dest_long;
    final int radious = 1000;
    boolean isclicked = false;
    Location homelocation;
    List<Location> points;
    DatabaseHelper mDatabase;
    boolean isDrag, isMain;
    public static boolean directionRequested;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    int id;
    int plcaevisit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_and_distance);
        initMap();
        getUserLocation();
        mDatabase = new DatabaseHelper(this);
        points = new ArrayList<>();
        if (!checkPermission()) {
            requestPermission();
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            //getUserLocation();
        }
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        dest_lat = intent.getDoubleExtra("lat", 6);
        dest_long = intent.getDoubleExtra("longi", 6);
        isDrag = intent.getBooleanExtra("edit", false);

        isMain = intent.getBooleanExtra("isMain", false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setHomeMarker();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }
    private boolean checkPermission() {
        int status = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return status == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (id!= -1){
            Button b1 = findViewById(R.id.btn_chhose);
            b1.setVisibility(View.GONE);
            LatLng userlatlong = new LatLng(dest_lat, dest_long);
            MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title("Your Destination").snippet("you are going there").draggable(isDrag).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            mMap.addMarker(markerOptions);
            //  Toast.makeText(DurationAndDistance.this, "lat"+dest_lat+"longi"+dest_long, Toast.LENGTH_SHORT).show();
        }
        if (isMain)
        {
            Button b1 = findViewById(R.id.btn_visited);
            b1.setVisibility(View.GONE);
        }
        if (isDrag){
            Button b1 = findViewById(R.id.btn_visited);
            b1.setVisibility(View.VISIBLE);
        }
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder geocoder = new Geocoder(DurationAndDistance.this);
                List<Address> addresses = new ArrayList<>();
                LatLng latLng = marker.getPosition();

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


                    if (!addresses.isEmpty()) {
                        String  address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
                        System.out.println(addresses.get(0).getAddressLine(0));
                        if  (mDatabase.updatePlace(id,addresses.get(0).getLocality(),marker.getPosition().longitude,addresses.get(0).getAddressLine(0),marker.getPosition().latitude,plcaevisit)){
                            Toast.makeText(DurationAndDistance.this, "addres;"+marker.getPosition().latitude, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  mDatabase.updatePlace(id,marker.getTitle(),marker.getPosition().latitude,marker.getSnippet(),marker.getPosition().longitude);

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (points.size() == 2) {
                    mMap.clear();
                    points.clear();
                }
                Location location = new Location("You Will Be Here Soon");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;
                points.add(location);
                //setMarker
                setMarker(location);

            }
        });
    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();

    }

    private void setMarker(Location location) {
        LatLng userlatlong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title("Your Destination").snippet("you are going there").draggable(isDrag).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(markerOptions);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectordrawableResourse) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectordrawableResourse);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);


    }


    private void setHomeMarker() {

        locationCallback = new LocationCallback() {
            // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    homelocation = location;
                    lat = location.getLatitude();
                    longi = location.getLongitude();
                    LatLng userLoaction = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraPosition cameraPosition = CameraPosition.builder().target(userLoaction).zoom(15).bearing(0).tilt(45).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLoaction).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.map)));
                }
            }
        };
    }


    private void home() {
        if (homelocation != null) {
            lat = homelocation.getLatitude();
            longi = homelocation.getLongitude();
            LatLng userLoaction = new LatLng(homelocation.getLatitude(), homelocation.getLongitude());
            CameraPosition cameraPosition = CameraPosition.builder().target(userLoaction).zoom(15).bearing(0).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions().position(userLoaction).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.map)));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private String getDirectionUrl() {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin=" + lat + "," + longi);
        googleDirectionUrl.append("&destination=" + dest_lat + "," + dest_long);
        googleDirectionUrl.append("&key=AIzaSyCA2OkQ-Um2T3wd624ikeMbhW37otDlykw");
        Log.d("", "getDirectionUrl: " + googleDirectionUrl);
        System.out.println(googleDirectionUrl.toString());
        return googleDirectionUrl.toString();
    }


    public void btnClick(View view) {
        Object[] dataTransfer;
        String url;

        switch (view.getId()) {


            case R.id.btn_duration:
                dataTransfer = new Object[4];

                if (isclicked) {
                    lat = points.get(0).getLatitude();
                    longi = points.get(0).getLongitude();
                    dest_long = points.get(1).getLongitude();
                    dest_lat = points.get(1).getLatitude();
                    url = getDirectionUrl();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(points.get(0).getLatitude(), points.get(0).getLongitude());
                    dataTransfer[3] = new LatLng(points.get(1).getLatitude(), points.get(1).getLongitude());
                } else {
                    url = getDirectionUrl();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(dest_lat, dest_long);
                    dataTransfer[3] = new LatLng(homelocation.getLatitude(), homelocation.getLongitude());
                }
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                getDirectionsData.execute(dataTransfer);
//                if (view.getId() == R.id.btn_direction)
//                    directionRequested = false;
//                else
//                    directionRequested = true;
//                points.clear();
//                break;

        }
    }

    public void onClick(View view) {
        mMap.clear();
        isclicked = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Info");
        builder1.setMessage("First create Two points On the map");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void placeVisited(View view) {
        plcaevisit = 1;
        if(mDatabase.updateVisit(id,plcaevisit)){
            Toast.makeText(DurationAndDistance.this, "Place Visited", Toast.LENGTH_SHORT).show();
            System.out.println("id:"+id+"visited"+plcaevisit);

        }

    }
}
