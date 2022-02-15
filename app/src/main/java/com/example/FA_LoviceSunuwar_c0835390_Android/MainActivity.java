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
import android.database.Cursor;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    //GoogleMap mMap;
    double lat, longi, dest_lat, dest_long;
    final int radious = 1000;
    List<Address> addresses;
    String address;
    boolean isOk;
    Geocoder geocoder;
    Location location;
    boolean isMrkerClick = false;
    Marker mMarker;

    DatabaseHelper mDatabase;
    //get user lopcation

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();


        getUserLocation();
        mDatabase = new DatabaseHelper(this);

        if (!checkPermission()) {
            requestPermission();
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            //getUserLocation();
        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.map_type, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                location = new Location("You Will Be Here Soon");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;
                try {
                    setMarker(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    getAddress(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        mMap.setOnInfoWindowClickListener(this);


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

    private void getAddress(Location location) throws IOException {
        System.out.println("In Get Address");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String addDate = simpleDateFormat.format(calendar.getTime());

        geocoder = new Geocoder(this, Locale.getDefault());

        System.out.println("in geocoder");

        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        if (!addresses.isEmpty()) {
            address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
            System.out.println(addresses.get(0).getAddressLine(0));

            //Toast.makeText(MainActivity.this, "Employee is not addaed", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "Address:"+addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
    }


    private void setMarker(Location location) throws IOException {
        System.out.println("In SetMarker");
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        LatLng userlatlong = new LatLng(location.getLatitude(), location.getLongitude());
        if (!addresses.isEmpty()) {
            address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
            System.out.println(addresses.get(0).getAddressLine(0));
        }
        MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title(addresses.get(0).getLocality());
        markerOptions.snippet(addresses.get(0).getAddressLine(0));
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
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

    public void btnClick(View view) {
        Object[] dataTransfer = new Object[3];
        String url;
        GetNearPlaces getNearByPlaceData = new GetNearPlaces(this);
        switch (view.getId()) {

            case R.id.btn_restaurants:
                mMap.clear();
                url = getUrl(lat, longi, "restaurant");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "resturent";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "restaurant", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_museum:
                mMap.clear();
                url = getUrl(lat, longi, "museum");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "museum";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "museum", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_cafe:
                mMap.clear();
                url = getUrl(lat, longi, "cafe");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "cafe";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "cafe", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_library:
                mMap.clear();
                url = getUrl(lat, longi, "library");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "library";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "library", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_school:
                mMap.clear();
                url = getUrl(lat, longi, "school");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "school";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Schools", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_hospital:
                mMap.clear();
                url = getUrl(lat, longi, "hospital");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "hospital";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Hospital", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_getlocatiomn:
                getUserLocation();
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
                setHomeMarker();
                break;
            case R.id.btn_clear:
                mMap.clear();
                break;


            case R.id.btn_Fav_place:

                Intent intent = new Intent(this, FavPlaces.class);
                startActivity(intent);
                break;

           /* case R.id.btn_direction:
                Intent intent2 = new Intent(this, DurationAndDistance.class);
                intent2.putExtra("isMain",true);
                startActivity(intent2);
                break;*/

        }
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + radious);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&key=" + getString(R.string.api_key_places));
        Log.d("", "getUrl: "+googlePlaceUrl);
        return googlePlaceUrl.toString();

    }
    private void setHomeMarker() {
        locationCallback = new LocationCallback() {
            // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    lat = location.getLatitude();
                    longi = location.getLongitude();
                    LatLng userLoaction = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraPosition cameraPosition = CameraPosition.builder().target(userLoaction).zoom(15).bearing(0).tilt(45).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLoaction).title("Your Destination").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.map)));
                }
            }
        };
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.maptypeHYBRID:
//                if (mMap != null) {
//                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                    return true;
//                }
//            case R.id.maptypeNONE:
//                if (mMap != null) {
//                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//                    return true;
//                }
//            case R.id.maptypeNORMAL:
//                if (mMap != null) {
//                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                    return true;
//                }
//            case R.id.maptypeSATELLITE:
//                if (mMap != null) {
//                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                    return true;
//                }
//            case R.id.maptypeTERRAIN:
//                if (mMap != null) {
//                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                    return true;
//                }
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void loadPlaces() {
        Cursor cursor = mDatabase.getAllPlace();
        if (cursor.moveToFirst()) {

            do {
                System.out.println(cursor.getString(1));
            } while (cursor.moveToNext());

            cursor.close();
        }
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("MARKER: "+ marker.getTitle());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Add this Place to your favourites?");
        builder1.setCancelable(true);
        mMarker = marker;
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isOk = true;
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        String addDate = simpleDateFormat.format(calendar.getTime());
                        if (isOk && mDatabase.addFavPlace(mMarker.getTitle(), addDate, mMarker.getSnippet(), mMarker.getPosition().latitude, mMarker.getPosition().longitude)) {
                            Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                            isOk = false;

                        }
                    }
                });
        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}