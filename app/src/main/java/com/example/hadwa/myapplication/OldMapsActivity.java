package com.example.hadwa.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.List;

public class OldMapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, View.OnClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates;
    LatLng mLastLocation;
    LatLng StartLocation;
    private LatLng Dest1 = new LatLng(29.988428, 31.4389311);
    LatLng ClickM;
    String choosenMarker1;
    LatLng savedLocation;
    int stops=1;
    int check;


    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    public static final int REQUEST_CHECK_SETTINGS = 10;
    private static final LatLngBounds GUC_BOUNDS = new LatLngBounds(new LatLng(29.9842014, 31.4387794),
            new LatLng(29.9899635, 31.4445531));



    private BottomSheetBehavior mBehavior;
    private View mBottomSheet;
    private TextView BottomSheetText;
    /*private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior mDialogBehavior;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        polylines = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //NewText = (TextView)findViewById(R.id.WhichStop);

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


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        mBottomSheet = findViewById(R.id.BottomSheet_layout);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });




    }




    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Missing")
                        .setMessage("Please give the missing permissions for the app to function")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create().show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "Please provide location permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Admission = new LatLng(29.988428, 31.4389311);
        final LatLng B = new LatLng(29.9859256, 31.4386074);
        final LatLng C = new LatLng(29.9859929, 31.4392198);
        final Marker marker1 = mMap.addMarker(new MarkerOptions().position(Admission).title("Admission Building"));

        mMap.addMarker(new MarkerOptions().position(Admission).title("Admission Building"));
        mMap.addMarker(new MarkerOptions().position(B).title("B Building"));
        mMap.addMarker(new MarkerOptions().position(C).title("C Building"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMinZoomPreference(16);
        mMap.setLatLngBoundsForCameraTarget(GUC_BOUNDS);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {


                if(stops==1){
                    choosenMarker1=marker.getTitle();
                    StartLocation=mLastLocation;

                }if(stops==2) {

                    choosenMarker1+=" "+marker.getTitle();
                    StartLocation=ClickM;
                }

                GetRoutToMarker(marker.getPosition());
                BottomSheetText = mBottomSheet.findViewById(R.id.WhichStop);
                BottomSheetText.setText(choosenMarker1);
                showBottomSheetView();

                Button addStop = (Button) findViewById(R.id.AddStop);
                addStop.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        stops=2;
                        check=8;


                    }
                });
                stops=1;
                ClickM=marker.getPosition();


                //Log.d("Hadwaaa", marker.getTitle());
                return true;
            }
        });



        createLocationRequest();
        startLocationUpdates();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(GUC));

    }


    private void GetRoutToMarker(LatLng clickedMarker) {
        ClickM = clickedMarker;
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                // .waypoints(clickedMarker,new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                .waypoints(StartLocation, clickedMarker)
                .withListener(this)
                .build();
        routing.execute();


    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {

        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {
        if(check!=8) {
            if (polylines.size() > 0) {
                for (Polyline poly : polylines) {
                    poly.remove();
                }
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }


    @Override
    public void onClick(View v) {

    }

    private void showBottomSheetView() {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

       /* if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }*/
    }
}
