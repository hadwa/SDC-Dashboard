package com.example.hadwa.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class VisualizationActivity extends AppCompatActivity implements OnMapReadyCallback ,DirectionCallback {
    private static final LatLngBounds GUC_BOUNDS = new LatLngBounds(new LatLng(29.9842014, 31.4387794),
            new LatLng(29.9899635, 31.4445531));
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates;
    LocationRequest mLocationRequest;
    LatLng mLastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        getSupportActionBar().hide();
        //MapsFragment MapsFragment1 = new MapsFragment();
        //Fragment MapsFragment1 = getFragmentManager().findFragmentById(R.id.mapFragment);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_2, MapsFragment, "").commit();
       //View Map= findViewById(R.id.mapFragment);
        //Fragment fragment = getFragmentManager().findFragmentById(R.id.mapFragment);
//        GoogleMap map = ((SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.map)).getMap();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v("osama", "I reached callback");
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.v("osama", mLastLocation.toString() + "");
                }
            }

        };
        SupportMapFragment mGoogleMap = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map1));
        //MapFragment mGoogleMap = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        mGoogleMap.getMapAsync(this);


//        View bottomSheet=findViewById(R.id.BottomSheet_layout);
//        bottomSheet.setVisibility(View.GONE);

//        View bottomSheet2=findViewById(R.id.BottomSheet_layout2);
//        bottomSheet2.setVisibility(View.GONE);
        //MapsFragment.mMap.getUiSettings().setMyLocationButtonEnabled(false);






    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap = googleMap;
        mMap.setMinZoomPreference(16);
        mMap.setLatLngBoundsForCameraTarget(GUC_BOUNDS);
        LatLng latLng = new LatLng(29.9867788, 31.441697);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        createLocationRequest();
        startLocationUpdates();
    }



    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //checkLocationPermission();
            } else {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    
}
