package com.example.hadwa.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MapsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setTitle("Please choose your drop-off point");

        MapsFragment MapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_2, MapsFragment, "").commit();

    }





}
