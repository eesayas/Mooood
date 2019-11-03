package com.example.mooood;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    TextView socialSituation;
    ImageView imageUpload;
    Uri imageUri;

    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    TextView dateAndTimeMood;
    Button submitButton;

    //needed for creating MoodEvent later
    String moodDate;
    String moodTime;
    String moodEmotionalState;
    String moodReason;
    String moodSocialSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Creating a mood roster
        moodImages = new ArrayList<>();
        moodImages.add(new Emoticon("HAPPY", R.drawable.happy_cow_v2));
        moodImages.add(new Emoticon("SAD", R.drawable.sad_cow_v2));
        moodImages.add(new Emoticon("LAUGHING", R.drawable.laughing_cow_v2));
        moodImages.add(new Emoticon("IN LOVE", R.drawable.inlove_cow_v2));
        moodImages.add(new Emoticon("ANGRY" , R.drawable.angry_cow_v2));
        moodImages.add(new Emoticon("SICK" , R.drawable.sick_cow_v2));
        moodImages.add(new Emoticon("AFRAID", R.drawable.afraid_cow_v2));

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

        //upload image for reason
        //Resource: https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-2-image-chooser
        imageUpload = findViewById(R.id.image_reason);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

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

        //click listener for submit button
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //retreive remaining needed for Mood Event
                EditText reasonText = findViewById(R.id.reason);
                moodReason = reasonText.getText().toString();

                TextView socialSituationText = findViewById(R.id.social_situation);
                moodSocialSituation = socialSituationText.getText().toString();

                MoodEvent moodEvent = createMoodEventObject(moodDate, moodTime, moodEmotionalState, moodReason, moodSocialSituation);
                addMoodEventToDB(collectionReference, moodEvent);

                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                startActivity(intent);

            }
        });

    } //end of onCreate

    //methods for image upload
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

    //Date and Time Picker Dialog methods

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
            moodTime = new SimpleDateFormat("h mm a", Locale.getDefault()).format(calendar.getTime());

            //set TexView to correspond with input data
            dateAndTimeMood.setText(simpleDateFormat.format(calendar.getTime()));
        }
    };

    //creates the object MoodEvent
    private MoodEvent createMoodEventObject(String moodDate, String moodTime, String moodEmotionalState, String moodReason, String moodSocialSituation){
        return new MoodEvent(moodDate, moodTime, moodEmotionalState, moodReason, moodSocialSituation);
    }

    //adds MoodEvent object to db
    private void addMoodEventToDB(CollectionReference collectionReference, MoodEvent moodEvent){
        collectionReference
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
    }

}