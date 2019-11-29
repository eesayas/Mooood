package com.example.mooood;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearSmoothScroller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoodsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Debugging";

    private GoogleMap gmap;
    private ArrayList<MoodEvent> moodsList;
    private String locationAddress;

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
            if(mood.getLatitude()!=null && mood.getLongitude()!=null){
                System.out.println(mood.getLatitude());
                Double latitude = Double.parseDouble(mood.getLatitude());
                Double longitude = Double.parseDouble(mood.getLongitude());
                getAddress(MoodsMapActivity.this,latitude,longitude);
                String author = mood.getAuthor();
                String date = mood.getDate();
                String time = mood.getTime();
                String reason = mood.getReason();
                String situation = mood.getSocialSituation();
                String infoWindow;
                LatLng latlng = new LatLng(latitude,longitude);

                if(reason == null||reason.equals("")){
                    if(situation == null||situation.equals("")){
                        infoWindow=date+" "+time+"\n"+locationAddress;
                    }
                    else{
                        infoWindow=situation+"\n"+date+" "+time+"\n"+locationAddress;
                    }
                }
                else{
                    if(situation == null||situation.equals("")){
                        infoWindow=reason+"\n"+date+" "+time+"\n"+locationAddress;
                    }
                    else{
                        infoWindow=reason+"\n"+situation+"\n"+date+" "+time+"\n"+locationAddress;
                    }
                }

                if(mood.getEmotionalState().equals("HAPPY")){
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mooood_logo);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );

                }
                if(mood.getEmotionalState().equals("SAD")){
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.sad_cow_v1);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );

                }
                if(mood.getEmotionalState().equals("LAUGHING")){
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.laughing_cow_v1);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );
                }
                if(mood.getEmotionalState().equals("IN LOVE")){
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.in_love_cow_v1);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );
                }
                if(mood.getEmotionalState().equals("ANGRY")){
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.angry_cow_v1);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );
                }
                if(mood.getEmotionalState().equals("SICK")){
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.sick_cow_v1);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );
                }
                if(mood.getEmotionalState().equals("AFRAID")) {
                    int height = 150;
                    int width = 150;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.afraid_cow_v1);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Marker marker = gmap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(author)
                            .snippet(infoWindow)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    );
                }
            }
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
        gmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MoodsMapActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MoodsMapActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MoodsMapActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public void getAddress(Context context, double LATITUDE, double LONGITUDE) {

        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                Log.d(TAG, "getAddress:  address" + address);

                locationAddress = address;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MoodEvent> getMoodsList() {
        return moodsList;
    }
}
