package com.example.peter.goolemaplocationtest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";
    private boolean mLocationPermissionGranted = false;
    private static final int LocationPermissionRequestCode = 1234;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
    }

    private void getDeviceCurrentLocation(){
        Log.d(TAG, "instance initializer: get device current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Log.d(TAG, "onComplete: found current location");
                            Location currentlocation = (Location)task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude()),15f));
                            mMap.setMyLocationEnabled(true);
                        }else{
                            Log.d(TAG, "onComplete: current location can not be found");
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "instance initializer: SecurityException "+e.getMessage() );
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        for(String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,permissions,LocationPermissionRequestCode);
            }
        }
        Log.d(TAG, "getLocationPermission: Permissions Granted");
        mLocationPermissionGranted = true;
        initMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Ask for permissions");
        mLocationPermissionGranted = false;
        if (requestCode == LocationPermissionRequestCode ){
            if(grantResults.length > 0) {
                for(int i : grantResults){
                    if(i != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: Permissions Denied");
                        mLocationPermissionGranted = false;
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: Permissions Granted");
                mLocationPermissionGranted = true;
                initMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"Map is ready",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map initialized");
        mMap = googleMap;
        if(mLocationPermissionGranted)
            getDeviceCurrentLocation();
    }
}
