package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private static final String TAG = "Debugging: from ShowEventActivity - ";
    public static final String MOOD_EVENT = "Mood Event";

    RelativeLayout fullscreenLayout;
    String author, date ,time ,emotionalState ,socialSituation ,imageUrl ,reason ,latitude ,longitude ,locationAddress;
    TextView authorText, dateText, timeText, socialSituationText, reasonText;
    ImageView emoticon, imageReason;
    LinearLayout backBtn, moreDetailsLayout;
    String edit;
    Button editButton;
    Button backButton;
    MapView mapView;
    GoogleMap gmap;

    private static final String MAP_VIEW_BUNDLE_KEY="MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        fullscreenLayout = findViewById(R.id.fullscreen_ll);
        moreDetailsLayout = findViewById(R.id.more_detail_ll);

        makeFullscreen(fullscreenLayout);
//        setMinimumHeight(moreDetailsLayout);

        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOOD_EVENT);
        edit = intent.getStringExtra("bool");

        getValuesMoodEvent(moodEvent);

        getTextAndImageView();

        setTextAndImageView();

        editBtnClickListener(moodEvent);

        goBackListener();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.showMapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


    }

    /**
     * This is a listener for any element that directs User back to Feed
     */
    private void goBackListener(){
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * This get the status bar height in the device
     * @return int
     * This is the status bar height
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * This makes the first layout fullscreen
     */
    private void makeFullscreen(RelativeLayout layout){
        //get device screen height
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = screenHeight - getStatusBarHeight();

    }

    private void setMinimumHeight(LinearLayout layout){
        //get device screen height
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = screenHeight - getStatusBarHeight();

        layout.setMinimumHeight(params.height);

    }


    /**
     * This gets all needed values from MoodEvent to be displayed
     *
     * @param moodEvent This is the MoodEvent object
     */
    private void getValuesMoodEvent(MoodEvent moodEvent) {
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
        emoticon.setTag(new Emoticon(emotionalState, 2).getImageLink()); //for testing
        dateText.setText(date);
        timeText.setText(time);
        socialSituationText.setText(socialSituation);
        Picasso.get().load(imageUrl).into(imageReason);
        reasonText.setText(reason);
    }

    /**
     * This is a click listener for edit of MoodEvent. Redirect to EditEventActivity
     */
    private void editBtnClickListener(final MoodEvent moodEvent) {
        editButton = findViewById(R.id.edit_button);
        if (edit.equals("false")) {
            editButton.setEnabled(false);
            editButton.setVisibility(View.INVISIBLE);
        } else {
            editButton.setEnabled(true);
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ShowEventActivity.this, EditEventActivity.class);
                    intent.putExtra(MOOD_EVENT, moodEvent);
                    startActivity(intent);
                }
            });
        }
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

