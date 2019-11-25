package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This is responsible for showing all the details of a selected MoodEvent
 **/

public class ShowEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "For Testing";
    public static final String MOOD_EVENT = "Mood Event";

    String author;
    String date;
    String time;
    String emotionalState;
    String socialSituation;
    String imageUrl;
    String reason;
    String latitude;
    String longitude;
    String locationAddress;
    TextView authorText;
    ImageView emoticon;
    TextView dateText;
    TextView timeText;
    TextView socialSituationText;
    ImageView imageReason;
    TextView reasonText;

    Button editButton;

    MapView mapView;
    GoogleMap gmap;

    private static final String MAP_VIEW_BUNDLE_KEY="MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOOD_EVENT);

        getValuesMoodEvent(moodEvent);

        getTextAndImageView();

        setTextAndImageView();

        editBtnClickListener(moodEvent);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.showMapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


    }

    /**
     * This gets all needed values from MoodEvent to be displayed
     * @param moodEvent
     *     This is the MoodEvent object
     */
    private void getValuesMoodEvent(MoodEvent moodEvent){
        author = moodEvent.getAuthor();

        date = moodEvent.getDate();
        time = moodEvent.getTime();
        emotionalState = moodEvent.getEmotionalState();

        socialSituation = moodEvent.getSocialSituation();
        imageUrl = moodEvent.getImageUrl();
        reason = moodEvent.getReason();
        latitude = moodEvent.getLatitude();
        longitude = moodEvent.getLongitude();
        locationAddress = moodEvent.getAddress();
    }

    /**
     * This selects all TextView and ImageView from xml
     */
    private void getTextAndImageView(){
        authorText = findViewById(R.id.author);
        emoticon = findViewById(R.id.emoticon);
        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        socialSituationText = findViewById(R.id.social_situation);
        imageReason = findViewById(R.id.image_reason);
        reasonText = findViewById(R.id.reason);
    }

    /**
     * This sets the values of all selected TextView and ImageView from xml
     */
    private void setTextAndImageView(){
        authorText.setText(author);
        emoticon.setImageResource(new Emoticon(emotionalState, 2).getImageLink());
        dateText.setText(date);
        timeText.setText(time);
        socialSituationText.setText(socialSituation);
        Picasso.get().load(imageUrl).into(imageReason);
        reasonText.setText(reason);
    }

    /**
     * This is a click listener for edit of MoodEvent. Redirect to EditEventActivity
     */
    private void editBtnClickListener(final MoodEvent moodEvent){
        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowEventActivity.this, EditEventActivity.class);
                intent.putExtra(MOOD_EVENT, moodEvent);
                startActivity(intent);
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        Double mapLatitude=Double.parseDouble(latitude);
        Double mapLongitude=Double.parseDouble(longitude);

        final LatLng myLocation = new LatLng(mapLatitude, mapLongitude);
        CameraPosition.Builder camBuilder = CameraPosition.builder();
        camBuilder.bearing(0);
        camBuilder.tilt(0);
        camBuilder.target(myLocation);
        camBuilder.zoom(11);

        CameraPosition cp = camBuilder.build();

        gmap.addMarker(new MarkerOptions().position(myLocation).title(locationAddress));
        gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

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
}
