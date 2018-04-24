package com.example.hadwa.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

public class MapsActivity extends FragmentActivity  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        MapsFragment MapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_2, MapsFragment, "").commit();

    }





}
