package com.example.mooood;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreatePostActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;

    //Declare variables for later use
    ViewPager moodRoster;
    SwipeMoodsAdapter moodRosterAdapter;
    List<Integer> moodImages;

    TextView socialSituation;
    ImageView imageUpload;
    Uri imageUri;

    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    TextView dateAndTimeMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //Creating a mood roster
        moodImages = new ArrayList<>();
        moodImages.add(R.drawable.happy_cow_v2);
        moodImages.add(R.drawable.sad_cow_v2);
        moodImages.add(R.drawable.laughing_cow_v2);
        moodImages.add(R.drawable.inlove_cow_v2);
        moodImages.add(R.drawable.angry_cow_v2);
        moodImages.add(R.drawable.sick_cow_v2);
        moodImages.add(R.drawable.afraid_cow_v2);

        //adapter for mood roster
        moodRosterAdapter = new SwipeMoodsAdapter(moodImages, this);

        moodRoster = findViewById(R.id.mood_roster);
        moodRoster.setAdapter(moodRosterAdapter);

        //styling to show a glimpse of prev and next moods
        moodRoster.setClipToPadding(false);
        moodRoster.setPadding(250,0,250,0);
        moodRoster.setPageMargin(50);

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
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy h: mm a", Locale.getDefault());
        dateAndTimeMood = findViewById((R.id.date_and_time));
        dateAndTimeMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                new DatePickerDialog(CreatePostActivity.this, DateDataSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
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
            new TimePickerDialog(CreatePostActivity.this, TimeDataSet, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener TimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            dateAndTimeMood.setText(simpleDateFormat.format(calendar.getTime()));
        }
    };

}
