package com.example.mooood;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "For Testing";

    //Declare variables for later use
    ViewPager moodRoster;
    SwipeMoodsAdapter moodRosterAdapter;
    List<Emoticon> moodImages;

    //Firebase setup!
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("MoodEvents");
    private DocumentReference documentReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextView socialSituation;

    //for image upload
    ImageView imageUpload;
    Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;

    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    TextView dateAndTimeMood;
    Button submitButton;
    Button locationButton;

    //needed for creating MoodEvent later
    String moodAuthor;
    String moodDate;
    String moodTime;
    Date moodTimeStamp;
    String moodEmotionalState;
    String moodImageUrl;
    String moodReason;
    String moodSocialSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Accessing acountName
        Intent intent = getIntent();
        final String accountName = intent.getStringExtra("key");

        documentReference = db.collection("MoodEvents").document(accountName);

        //Creating a mood roster
        moodImages = new ArrayList<>();
        moodImages.add(new Emoticon("HAPPY", 2));
        moodImages.add(new Emoticon("SAD", 2));
        moodImages.add(new Emoticon("LAUGHING", 2));
        moodImages.add(new Emoticon("IN LOVE", 2));
        moodImages.add(new Emoticon("ANGRY", 2));
        moodImages.add(new Emoticon("SICK", 2));
        moodImages.add(new Emoticon("AFRAID", 2));

        //adapter for mood roster
        moodRosterAdapter = new SwipeMoodsAdapter(moodImages, this);

        moodRoster = findViewById(R.id.mood_roster);
        moodRoster.setAdapter(moodRosterAdapter);

        //styling to show a glimpse of prev and next moods
        moodRoster.setClipToPadding(false);
        moodRoster.setPadding(250,0,250,0);
        moodRoster.setPageMargin(50);

        moodEmotionalState = "HAPPY"; //default

        //click listener for Emoticon
        moodRoster.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //no need to use but must be here
            }

            @Override
            public void onPageSelected(int position) {
                moodEmotionalState = moodImages.get(position).getEmotionalState();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //no need to use but must be here
            }
        });

        //situation fragment
        socialSituation = findViewById(R.id.social_situation);
        socialSituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SocialSituationFragment().show(getSupportFragmentManager(), "ADD_SOCIAL_SITUATON");
            }
        });

        //==============================================================================================
        // IMAGE UPLOAD SETUP
        // Resource: https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-2-image-chooser
        //==============================================================================================

        // click listener for Image Upload
        imageUpload = findViewById(R.id.image_reason);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //
        storageReference = FirebaseStorage.getInstance().getReference("reason_image");
        databaseReference = FirebaseDatabase.getInstance().getReference("reason_image");


        //==============================================================================================
        // DATE AND TIME PICKER DIALOG FRAGMENT click listener
        //==============================================================================================

        //access date and time picker fragments
        //Resource: https://github.com/Kiarasht/Android-Templates/tree/master/Templates/DatePickerDialog
        simpleDateFormat = new SimpleDateFormat("MMM/dd/yyyy h:mm a", Locale.getDefault());
        dateAndTimeMood = findViewById((R.id.date_and_time));
        dateAndTimeMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                new DatePickerDialog(CreateEventActivity.this, DateDataSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
            }
        });

        //==============================================================================================
        // LOCATION BUTTON click listener
        //==============================================================================================
        locationButton=findViewById(R.id.location_button);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });




        //==============================================================================================
        // SUBMISSION
        //==============================================================================================

        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //retrieve remaining needed for Mood Event
                EditText reasonText = findViewById(R.id.reason);
                moodReason = reasonText.getText().toString();

                TextView socialSituationText = findViewById(R.id.social_situation);
                moodSocialSituation = socialSituationText.getText().toString();

                moodAuthor = accountName;

                //create timestamp
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm:ss a");

                try {
                    moodTimeStamp = simpleDateFormat.parse(moodDate + ' ' + moodTime);

                } catch (ParseException e){
                    e.printStackTrace();
                }

//                //upload image
                if(uploadTask != null && uploadTask.isInProgress()){
                    Log.d(TAG, "uploading in progress");
                } else{
                    uploadImage();
                }

//                MoodEvent moodEvent = new MoodEvent(moodDate, moodTime, moodEmotionalState, moodImageUrl, moodReason, moodSocialSituation);
//                addMoodEventToDB(collectionReference, moodEvent);
//
//                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
//                startActivity(intent);

            }
        });

    } //end of onCreate

    //==============================================================================================
    // IMAGE UPLOAD METHODS
    //==============================================================================================

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(imageUpload);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage(){
        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //set up progress bar on later dev
                        }
                    }, 500);

                    //Add Toast message here for upload success

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    UploadImage uploadImage = new UploadImage(downloadUrl.toString());
                    moodImageUrl = uploadImage.getImageUrl();
                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(uploadImage);

                    //submit to db
                    submitMoodEventToDB();


                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "failed to upload image");
                }
            });
        }
    }

    //==============================================================================================
    // DATE AND TIME PICKER DIALOG FRAGMENT
    //==============================================================================================

    /* After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately */
    private final DatePickerDialog.OnDateSetListener DateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //get Date
            moodDate = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(calendar.getTime());

            new TimePickerDialog(CreateEventActivity.this, TimeDataSet, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener TimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            //get Time
            moodTime = new SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(calendar.getTime());

            //set TexView to correspond with input data
            dateAndTimeMood.setText(simpleDateFormat.format(calendar.getTime()));
        }
    };
    //==============================================================================================
    // GOOGLE MAPS LOCATION ACCESS
    //==============================================================================================
    private void openMaps(){
        Intent intent = new Intent(CreateEventActivity.this,MapsActivity.class);
        CreateEventActivity.this.startActivity(intent);
    }



    //adds MoodEvent object to db
    private void addMoodEventToDB(DocumentReference documentReference, MoodEvent moodEvent){
        Log.d("debugging", "here");

        documentReference.collection("MoodActivities")
                .document()
                .set(moodEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //These are a method which gets executed when the the task is successful
                        Log.d(TAG, "Data addition successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Not successful =(
                        Log.d(TAG, "Data addition failed" + e.toString());
                    }
                });
        Log.d("debugging", "done");

    }

    private void submitMoodEventToDB(){

        Log.d(TAG, "whatsup " + moodAuthor);
        MoodEvent moodEvent = new MoodEvent(moodAuthor, moodDate, moodTime, moodEmotionalState, moodImageUrl, moodReason, moodSocialSituation);
        moodEvent.setTimeStamp(moodTimeStamp);
        addMoodEventToDB(documentReference, moodEvent);
        Log.d("debugging", "back to feed");

        finish();
    }

}