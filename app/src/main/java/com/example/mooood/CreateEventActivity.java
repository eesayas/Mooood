package com.example.mooood;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * FILE PURPOSE: This is for creating a new mood event
 **/

public class CreateEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Debugging";

    //Declare variables for later use
    private ViewPager moodRoster;
    private SwipeMoodsAdapter moodRosterAdapter;
    private List<Emoticon> moodImages;

    //for UX
    private ViewFlipper viewFlipper;
    private ImageView chosenEmoticon;

    //Firebase setup!
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    private TextView socialSituation, moodIndicator, dateAndTimeMood;
    private EditText reason;

    //for image upload
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String currentPhotoPath;
    private ImageView imageUpload;
    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;

    //for time
    SimpleDateFormat simpleDateFormat;
    private Calendar calendar;

    //for toggle switches
    private SwitchCompat toggleImagePreview, toggleGPSPreview;
    private LinearLayout gpsPreviewCont, imgReasonCont;

    //For location services inside the activity
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private GoogleMap gmap;
    private FusedLocationProviderClient fusedLocationClient;
    private String locationAddress;
    private Double locationLatitude = 53.5;
    private Double locationLongitude = -113.5;
    private Marker myMarker;
    private LatLng moodLocation;

    private Button cancelButton, selectEmoticonBtn, submitButton;
    private LinearLayout backButton;

    //the code will populate this
    protected MoodEvent moodEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //connecting variables to their respective element (should be in setup, need to edit CreateEventActivity)
        reason = findViewById(R.id.reason);
        socialSituation = findViewById(R.id.social_situation);
        imageUpload = findViewById(R.id.image_reason);
        dateAndTimeMood = findViewById((R.id.date_and_time));
        moodIndicator = findViewById(R.id.emotion_indicator);
        viewFlipper = findViewById(R.id.view_flipper);
        chosenEmoticon = findViewById(R.id.chosen_emoticon);
        gpsPreviewCont = findViewById(R.id.gps_preview_cont);
        imgReasonCont = findViewById(R.id.img_reason_cont);
        toggleImagePreview = findViewById(R.id.toggle_image_preview);
        toggleGPSPreview = findViewById(R.id.toggle_gps_preview);
        cancelButton = findViewById(R.id.cancel_button);
        backButton = findViewById(R.id.back_btn);

        //Acquire the account name of the current User
        Intent intent = getIntent();
        final String accountName = intent.getStringExtra("key");

        //proper doc ref in db according to account name
        documentReference = db.collection("MoodEvents").document(accountName);

        //Init a MoodEvent that will be further constructed by setters
        moodEvent = new MoodEvent();

        moodEvent.setAuthor(accountName);

        //Setup the CreateEventActivity [VIEW]
        createMoodRoster();
        swipeMoodAdapterSetup();
        customSwipeMoodStyling();
        setCurrentDateAndTime();
        backBtnListener();

        moodSelection();

        emoticonSelectBtnListener();

        //Invoke methods for selection of MoodEvent details

        socialSituationClickListener();
        dateAndTimePickerClickListener();

        togglePreviews();

        //setup for image upload
        storageReference = FirebaseStorage.getInstance().getReference("reason_image");
        databaseReference = FirebaseDatabase.getInstance().getReference("reason_image");

        imageUploadClickListener();
        //==============================================================================================
        // LOCATION services
        //==============================================================================================
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.createMapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        //SUBMISSION
        submitBtnClickListener();
    } //end of onCreate


    // VIEW FLIPPER
    public void previousView(View view) {
        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.showPrevious();
    }

    public void nextView(View view) {
        viewFlipper.setInAnimation(this, R.anim.slide_in_right);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        viewFlipper.showNext();
    }

    /**
     * This will allow user to edit their chosen emoticon
     */
    private void chosenEmoticonListener(){
        chosenEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousView(view);
            }
        });
    }

    /**
     * Redirects User to UserFeedActivity
     */
    private void backBtnListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //==============================================================================================
    // Setup CreateEventActivity [VIEW]
    //==============================================================================================

    /**
     * This creates the actual mood roster and populates it with Emoticons
     **/
    private void createMoodRoster() {
        moodImages = new ArrayList<>();
        moodImages.add(new Emoticon("HAPPY", 2));
        moodImages.add(new Emoticon("SAD", 2));
        moodImages.add(new Emoticon("LAUGHING", 2));
        moodImages.add(new Emoticon("IN LOVE", 2));
        moodImages.add(new Emoticon("ANGRY", 2));
        moodImages.add(new Emoticon("SICK", 2));
        moodImages.add(new Emoticon("AFRAID", 2));
    }

    /**
     * This is the setup for the adapter that contains all the emoticons
     **/
    private void swipeMoodAdapterSetup() {
        moodRosterAdapter = new SwipeMoodsAdapter(moodImages, this);
        moodRoster = findViewById(R.id.mood_roster);
        moodRoster.setAdapter(moodRosterAdapter);
    }

    /**
     * This just customs the mood roster so the next and previous emoticon can be seen partially
     **/
    private void customSwipeMoodStyling() {
        moodRoster.setClipToPadding(false);
        moodRoster.setPadding(150, 0, 150, 0);
        imageUpload.setAdjustViewBounds(true);
    }

    //==============================================================================================
    // Methods for Selection of MoodEvent details
    //==============================================================================================

    /**
     * This is a listener of the selections of mood from Mood Roster
     **/
    private void moodSelection() {

        moodEvent.setEmotionalState("HAPPY"); //default

        //click listener for Emoticon
        moodRoster.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //no need to use but must be here
            }

            @Override
            public void onPageSelected(int position) {
                String emotion = moodImages.get(position).getEmotionalState();
                moodEvent.setEmotionalState(emotion); //for the object to be created
                moodIndicator.setText(emotion); //for the indicator in xml

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //no need to use but must be here
            }
        });

    }

    private void emoticonSelectBtnListener() {
        selectEmoticonBtn = findViewById(R.id.select_emoticon_btn);

        selectEmoticonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, moodEvent.getEmotionalState());

                chosenEmoticon.setImageResource(new Emoticon(moodEvent.getEmotionalState(), 1).getImageLink());
                nextView(view);
            }
        });
    }

    /**
     * This is a listener for social situation
     **/
    private void socialSituationClickListener() {
        socialSituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialSituationOptions();
            }
        });
    }

    /**
     * This contains the options for social situation
     **/
    private void socialSituationOptions() {
        final CharSequence[] options = {
                "Alone",
                "With Someone",
                "With Group",
                "With Crowd"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);

        builder.setTitle("Choose a Social Situation");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                socialSituation.setText(options[i].toString());
                moodEvent.setSocialSituation(options[i].toString());
            }
        });

        builder.show();
    }

    /**
     * This sets the date/time field to the current time and date
     */
    private void setCurrentDateAndTime() {
        calendar = Calendar.getInstance(); //now

        simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a", Locale.getDefault());

        moodEvent.setDate(new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(calendar.getTime()));
        moodEvent.setTime(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.getTime()));

        String displayText = moodEvent.getDate() + " at " + moodEvent.getTime();
        dateAndTimeMood.setText(displayText);
    }

    /**
     * This is accesses the fragment that is used to obtain date and time of MoodEvent
     */
    private void dateAndTimePickerClickListener() {
        simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a", Locale.getDefault());
        dateAndTimeMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                new DatePickerDialog(CreateEventActivity.this, DateDataSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    // After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately
    private final DatePickerDialog.OnDateSetListener DateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //get Date
            moodEvent.setDate(new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(calendar.getTime()));

            //go to TimePicker
            new TimePickerDialog(CreateEventActivity.this, TimeDataSet, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    // After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView
    private final TimePickerDialog.OnTimeSetListener TimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            //get Time
            moodEvent.setTime(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.getTime()));

            //set TexView to correspond with input data
            String displayText = moodEvent.getDate() + " at " + moodEvent.getTime();
            dateAndTimeMood.setText(displayText);
        }
    };

    //TOGGLES

    /**
     * This toggles (shows/hides) the previewers for gps and image upload
     */
    private void togglePreviews() {

        toggleImagePreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    imageUpload.setVisibility(View.VISIBLE);

                } else {
                    imageUpload.setVisibility(View.GONE);

                    //RESET PREVIEW
                }
            }
        });

        toggleGPSPreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    gpsPreviewCont.setVisibility(View.VISIBLE);

                } else {
                    gpsPreviewCont.setVisibility(View.GONE);
                }
            }
        });
    }

    //==========================================================================================
    // UPLOAD IMAGE METHODS (Note: Use design pattern to put all this into a different file)
    //==========================================================================================

    /**
     * This opens the image gallery of the phone for image upload
     */
    private void imageUploadClickListener() {
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraOrGallery();
            }
        });
    }

    /**
     * This gives a choice between camera or photo gallery
     **/
    private void cameraOrGallery() {
        final CharSequence[] options = {
                "Take a photo",
                "Choose from gallery"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);

        builder.setTitle("Select");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Take a photo")) {
                    dispatchTakePictureIntent();
                } else if (options[i].equals("Choose from gallery")) {
                    openFileChooser();
                }
            }
        });

        builder.show();
    }

    /**
     * This is for selecting image from photo gallery
     **/
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * This creates a temp file for the camera taken photo
     **/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * This is for capturing image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    /**
     * This is for displaying the preview for image provided by User
     *
     * @param requestCode This is the request code from openFileChooser or dispatchTakePictureIntent
     * @param resultCode  This is the result code from openFileChooser or dispatchTakePictureIntent
     * @param data        This is the data from openFileChooser or dispatchTakePictureIntent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if from photo gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageUpload);

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) { //if from camera
            galleryAddPic();

            File f = new File(currentPhotoPath);
            imageUri = Uri.fromFile(f);
            Picasso.get().load(imageUri).into(imageUpload);

        }
    }

    /**
     * This returns a string which represents the extension of the given uri
     *
     * @param uri This is the uri of the image to be uploaded
     * @return The String extension of the uri
     **/
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * This saves the image to the gallery of the android device
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * This uploads the image into Firebase (note: this is async)
     **/
    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //set up progress bar on later dev
                                }
                            }, 500);
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "failed to upload image");
                        }
                    });

            getUploadedImageUrl(uploadTask, fileReference);

        } else {
            Log.d(TAG, "IMAGE CAPTURE HAS NO URI");
        }

    }

    /**
     * This gets the url of the uploaded image
     */
    private void getUploadedImageUrl(StorageTask uploadTask, final StorageReference imageReference) {
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    moodEvent.setImageUrl(downloadUri.toString());

                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(downloadUri.toString());

                    submitMoodEventToDB(documentReference, moodEvent);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    //==========================================================================================
    // GEOLOCATION METHODS
    //==========================================================================================

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
        gmap.setIndoorEnabled(true);
        gmap.setMyLocationEnabled(true);

        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        //TODO:I couldn't figure it out, but I suggest we find a way to decouple so many of these functions from this listener.
        // Response: Yes will apply MVC before we submit - eesayas
        //Gets the location from gps,places a marker at said location, gives it the title of the address or a default address, and moves the camera to the marker's position.
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            locationLatitude = location.getLatitude();
                            locationLongitude = location.getLongitude();

                            CameraPosition.Builder camBuilder;
                            camBuilder = CameraPosition.builder();
                            camBuilder.bearing(0);
                            camBuilder.tilt(0);
                            camBuilder.zoom(11);
                            camBuilder.target(new LatLng(locationLatitude, locationLongitude));
                            CameraPosition cp = camBuilder.build();
                            gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                            //Try to call getAddress. If it fails, the address will just be set to "current location"
                            try {
                                getAddress(CreateEventActivity.this, locationLatitude, locationLongitude);
                            } catch (Exception e) {
                                e.printStackTrace();
                                locationAddress = "Current Location";
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(locationLatitude, locationLongitude)).title(locationAddress);
                            myMarker = gmap.addMarker(markerOptions);
                        }
                    }
                });
        //Updates marker and moves the camera whenever a new marker is placed on the map by long clicking on the map.
        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                locationLatitude = latLng.latitude;
                locationLongitude = latLng.longitude;

                //Try to call getAddress. If it fails, the address will just be set to "current location"
                try {
                    getAddress(CreateEventActivity.this, locationLatitude, locationLongitude);
                } catch (Exception e) {
                    e.printStackTrace();
                    locationAddress = "Current Location";
                }

                //Updates marker's address and position.
                myMarker.setPosition(latLng);
                myMarker.setTitle(locationAddress);

                //Update the camera's position to the new marker's position
                gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    //getAddress updates the location address with a geocoded address string that contains country,state/province,city, postal code, street number, street name.
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
        } catch (IOException e) {
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

    public LatLng getMoodLocation() {
        return moodLocation;
    }

    public void setMoodLocation(LatLng moodLocation) {
        this.moodLocation = moodLocation;
    }

    /**
     * This will set the longitude and latitude of MoodEvent
     */
    private void obtainCoordinates() {
        String latitudeStr = Double.toString(locationLatitude);
        String longitudeStr = Double.toString(locationLongitude);
        moodEvent.setLatitude(latitudeStr);
        moodEvent.setLongitude(longitudeStr);
        moodEvent.setAddress(locationAddress);
    }

    //==========================================================================================
    // ASSEMBLING MOODEVENT AND SUBMITTING IT TO DB
    //==========================================================================================

    /**
     * This checks if GPS or IMAGE UPLOAD is switched on or off and deletes values accordingly
     */
    private void checkToggles() {
        if (!toggleImagePreview.isChecked()) {
            moodEvent.setImageUrl(null);
        }

        if (!toggleGPSPreview.isChecked()) {
            moodEvent.setLatitude(null);
            moodEvent.setLongitude(null);
            moodEvent.setAddress(null);
        }
    }


    /**
     * This is a click listener for submit button. This actually submits the new MoodEvent ito DB
     *
     * @params accountName
     * This is the accountName of the user that is logged in
     */
    private void submitBtnClickListener() {
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //necessary methods before MoodEvent submission
                createTimeStamp();

                //order is important
                obtainCoordinates();
                checkToggles();
                boolean correctReason = obtainReason();

                if (uploadTask != null && uploadTask.isInProgress()) {
                    Log.d(TAG, "Upload in Progress");
                    Toast.makeText(CreateEventActivity.this,"Upload in progress. Please Wait.", Toast.LENGTH_SHORT).show();

                } else if (uploadTask == null && imageUri == null && correctReason) {
                    Log.d(TAG, "Image Capture fail");
                    submitMoodEventToDB(documentReference, moodEvent);

                } else if(!correctReason){
                    Toast.makeText(CreateEventActivity.this,"Reason should be no more than 20 characters or 3 words", Toast.LENGTH_LONG).show();

                } else {
                    uploadImage();
                }

            }
        });
    }

    /**
     * This is will be used for counting words in reason (must be <= 3)
     *
     * @param input This is the string whose words will be counted
     * @return int
     * This is the total word count
     */
    public static int countWords(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        String[] words = input.split("\\s+");
        return words.length;
    }

    /**
     * This obtains the reason for MoodEvent
     **/
    private boolean obtainReason() {

        //if reason is not empty and it violates US 02.01.01
        if (reason.getText().toString().length() > 20 || countWords(reason.getText().toString()) > 3) {
            return false;

        } else {

            //acceptable conditions for submission
            if (reason.getText().toString().equals("")) {
                moodEvent.setReason(null);
            } else {
                moodEvent.setReason(reason.getText().toString());
            }
        }

        return true;
    }


    /**
     * This creates timestamp for moodEvent
     **/
    private void createTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");

        try {
            moodEvent.setTimeStamp(simpleDateFormat.parse(moodEvent.getDate() + ' ' + moodEvent.getTime()));

        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    /**
     * This adds the MoodEvent to DB
     * **/
    private void submitMoodEventToDB(DocumentReference documentReference, MoodEvent moodEvent){

        documentReference.collection("MoodActivities")
                .document()
                .set(moodEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //These are a method which gets executed when the the task is successful
                        Log.d(TAG, "CreateEventActivity - Data addition successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Not successful =(
                        Log.d(TAG, "CreateEventActivity - Data addition failed" + e.toString());
                    }
                });
        finish();

    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }
}