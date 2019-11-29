package com.example.mooood;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FeedMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gmap;
    private ArrayList<MoodEvent> moodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        moodsList = intent.getParcelableArrayListExtra("moodList");



    }

    public void populateLocationsData(GoogleMap gmap){
        for (int counter = 0; counter < moodsList.size(); counter++) {
            MoodEvent mood = moodsList.get(counter);
            Double latitude = Double.parseDouble(mood.getLatitude());
            Double longitude = Double.parseDouble(mood.getLongitude());
            String address = mood.getAddress();
            String author = mood.getAuthor();
            LatLng latlng = new LatLng(latitude,longitude);
            gmap.addMarker(new MarkerOptions().position(latlng).title(
                    author+" seen at: "+address));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        LatLng edmonton = new LatLng(53.5, -113.5);

        CameraPosition.Builder camBuilder = CameraPosition.builder();
        camBuilder.bearing(0);
        camBuilder.tilt(0);
        camBuilder.target(edmonton);
        camBuilder.zoom(11);

        // Add all markers
        populateLocationsData(gmap);

        CameraPosition cp = camBuilder.build();
        gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }
}
