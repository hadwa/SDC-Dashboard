package com.example.hadwa.myapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, RoutingListener, View.OnClickListener{

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
    private View view;
    private TextView UpperSheetText;


    List<String> Markers;
    RecyclerView recyclerView;
    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.maps_fragment, container, false);

        polylines = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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

//        BottomSheetText.setText("Pick a drop-off location");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        Markers = new ArrayList<>();
        RecyclerListAdapter adapter = new RecyclerListAdapter(getActivity(),Markers);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callBack = new ItemDragHelper(adapter);
        ItemTouchHelper dragHelper = new ItemTouchHelper(callBack);
        dragHelper.attachToRecyclerView(recyclerView);


        return view;

    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
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

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
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

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getContext(), "Please provide location permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void onPause() {
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
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        BottomSheetText = getActivity().findViewById(R.id.WhichStop);
        BottomSheetText.setText("Pick a drop-off location");
        BottomSheetText.setAlpha((float) 0.54);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMinZoomPreference(16);
        mMap.setLatLngBoundsForCameraTarget(GUC_BOUNDS);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMarkerClick(final Marker marker) {

                Markers.add(marker.getTitle());

                if(stops==1){
                    choosenMarker1=marker.getTitle();
                    StartLocation=mLastLocation;

                }if(stops==2) {

                    choosenMarker1+=" "+marker.getTitle();
                    StartLocation=ClickM;
                }

                GetRoutToMarker(marker.getPosition());
                BottomSheetText.setText(choosenMarker1);
                BottomSheetText.setAlpha((float) 0.87);
  //              UpperSheetText=getActivity().findViewById(R.id.Upper_sheetText);
  //              UpperSheetText.setText("Proceed or click ADD STOP for other destinations");

                //showBottomSheetView();

//                Button addStop = (Button) getActivity().findViewById(R.id.AddStop);
//                addStop.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                       // mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        UpperSheetText.setText("Choose other stop");
//                        stops=2;
//                        check=8;
//
//
//                    }
//                });
//                addStop.setOnDragListener(new View.OnDragListener() {
//                    @Override
//                    public boolean onDrag(View v, DragEvent event) {
//
//                        return false;
//                    }
//                });
                stops=1;
                ClickM=marker.getPosition();



                //Log.d("Hadwaaa", marker.getTitle());
                return true;
            }
        });
        Button Start = (Button) getActivity().findViewById(R.id.start);
        Start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


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
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
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
