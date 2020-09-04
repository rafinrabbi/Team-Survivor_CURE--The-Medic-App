package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {


    private MapView mapView;
    private GoogleMap gmap;
    private Button setLo, getLo;
    private LocationManager lm;
    private FusedLocationProviderClient mFusedLocationClient;
    private int PERMISSION_ID = 44;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        setLo = (Button) findViewById(R.id.SET_LOCATION);
        getLo = (Button) findViewById(R.id.GET_LOCATION);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.locationView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        setLo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("LAT", lat);
                returnIntent.putExtra("LON", lon);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        getLo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getLastLocation();
                }catch (Exception e){
                    Toast.makeText(MapViewActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLastLocation()
    {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                                new OnCompleteListener<Location>() {

                                    @Override
                                    public void onComplete(
                                            @NonNull Task<Location> task)
                                    {
                                        Location location = task.getResult();
                                        if (location == null) {
                                            requestNewLocationData();
                                        }
                                        else {
                                            LatLng ln = new LatLng(location.getLatitude(), location.getLongitude());
                                            lat = ln.latitude;
                                            lon = ln.longitude;
                                            changeCamera(ln);
                                        }
                                    }
                                });
            }

            else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData()
    {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest
                = new LocationRequest();
        mLocationRequest.setPriority(
                LocationRequest
                        .PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        mFusedLocationClient
                .requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper());
    }

    private LocationCallback
            mLocationCallback
            = new LocationCallback() {

        @Override
        public void onLocationResult(
                LocationResult locationResult)
        {
            Location mLastLocation
                    = locationResult
                    .getLastLocation();
            LatLng ln = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions()
    {
        return ActivityCompat
                .checkSelfPermission(
                        this, Manifest.permission
                                .ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission
                                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        /* ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        */
    }

    // method to requestfor permissions
    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION,
                        Manifest.permission
                                .ACCESS_FINE_LOCATION },
                PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled()
    {
        LocationManager
                locationManager
                = (LocationManager)getSystemService(
                Context.LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(
                        LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager
                    .PERMISSION_GRANTED) {

                getLastLocation();
            }
        }
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

    }

    private void changeCamera(LatLng pos){
        if(gmap == null){
            Toast.makeText(this, "Map not ready.", Toast.LENGTH_LONG).show();
            return;
        }
        String lat = String.valueOf(pos.latitude);
        String lon = String.valueOf(pos.longitude);
        Toast.makeText(MapViewActivity.this, lat+" "+lon, Toast.LENGTH_LONG).show();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        gmap.addMarker(markerOptions);

        gmap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}