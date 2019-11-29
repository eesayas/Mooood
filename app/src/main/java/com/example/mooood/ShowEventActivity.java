package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
    private static final String TAG = "ShowEventActivity";
    public static final String MOOD_EVENT = "Mood Event";

    private RelativeLayout fullscreenLayout;
    private String author, date ,time ,emotionalState ,socialSituation ,imageUrl ,reason ,latitude ,longitude ,locationAddress;
    private TextView authorText, dateText, timeText, socialSituationText, reasonText, addressTxt;
    private ImageView emoticon, imageReason;
    private LinearLayout backArrow, moreDetailsLayout, mapCont, socialSituationCont, reasonCont;
    private String edit;
    private Button editButton, backButton;
    private MapView mapView;
    private GoogleMap gmap;

    private static final String MAP_VIEW_BUNDLE_KEY="MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        //connect to xml
        fullscreenLayout = findViewById(R.id.fullscreen_ll);
        moreDetailsLayout = findViewById(R.id.more_detail_ll);
        socialSituationCont = findViewById(R.id.social_situation_cont);
        reasonCont = findViewById(R.id.reason_cont);
        backArrow = findViewById(R.id.back_btn);
        backButton = findViewById(R.id.go_back_btn);
        addressTxt = findViewById(R.id.address_txt);

        //make the first Linear Layout fullScreen
        makeFullscreen(fullscreenLayout);

        //get the object to edit
        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOOD_EVENT);
        edit = intent.getStringExtra("bool");

        //populate ShowEvent with data
        getValuesMoodEvent(moodEvent);
        getTextAndImageView();
        setTextAndImageView();

        editBtnClickListener(moodEvent);
        goBackListener();

        //================================================
        // map retrieval functionality
        //================================================
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.showMapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        mapCont = findViewById(R.id.map_cont);

        removeReason();
        removeSocialSituation();
        removeMap();

    }

    /**
     * This is a listener for any element that directs User back to Feed
     */
    private void goBackListener(){

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
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
        imageReason.setTag(imageUrl);
        String reasonQuote =  "\" "  + reason + " \"";
        reasonText.setText(reasonQuote);
        reasonText.setTag(reason);
        addressTxt.setText(locationAddress);
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

    /**
     * This removes reason if it is null
     */
    private void removeReason(){
        if(reason == null){
            reasonText.setVisibility(View.GONE);
        }

        if(imageUrl == null){
            imageReason.setVisibility(View.GONE);
        }

        if(imageUrl == null && reason == null){
            reasonCont.setVisibility(View.GONE);
        }

    }

    /**
     * This removes the social situation layout if ss is null
     */
    private void removeSocialSituation(){
        if(socialSituation == null){
            socialSituationCont.setVisibility(View.GONE);
        }
    }

    /**
     * This removes the map layout in ShowEventActivity if lat and long are null
     */
    private void removeMap(){
        if(latitude == null || longitude == null || locationAddress == null){
            mapCont.setVisibility(View.GONE);
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

        double mapLatitude;
        double mapLongitude;

        if (latitude != null || longitude != null) {
            mapLatitude = Double.parseDouble(latitude);
            mapLongitude = Double.parseDouble(longitude);

        } else{
            mapLatitude = 0;
            mapLongitude = 0;
        }

        final LatLng myLocation = new LatLng(mapLatitude, mapLongitude);
        CameraPosition.Builder camBuilder = CameraPosition.builder();
        camBuilder.bearing(0);
        camBuilder.tilt(0);
        camBuilder.target(myLocation);
        camBuilder.zoom(11);

        CameraPosition cp = camBuilder.build();

        getAddress(ShowEventActivity.this,mapLatitude,mapLongitude);

        gmap.addMarker(new MarkerOptions().position(myLocation).title(locationAddress));
        gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

    }

    //getAddress updates the location address with a geocoded address string that contains country,state/province,city, postal code, street number, street name.
    public void getAddress(Context context, double LATITUDE, double LONGITUDE) {

        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

//                Log.d(TAG, "getAddress:  address" + address);

                locationAddress = address;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}

