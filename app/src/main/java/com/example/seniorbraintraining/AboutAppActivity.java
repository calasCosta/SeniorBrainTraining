package com.example.seniorbraintraining;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutAppActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map1);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady( GoogleMap googleMap) {
        mMap = googleMap;

        //Add a marker in some place and move the camera
        LatLng place = new LatLng(38.52227, -8.83881);
        mMap.addMarker(new MarkerOptions().position(place).title("IPS - SeniorBrainTraining"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
    }
}