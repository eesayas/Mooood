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
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "For Testing";
    public static final String MOODEVENT = "Mood Event";

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
    EditText reason;

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

    //needed for creating MoodEvent later
    String moodDate;
    String moodTime;
    String moodEmotionalState;
    String moodImageUrl;
    String moodReason;
    String moodSocialSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Accessing Document
        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOODEVENT);

        moodRoster = findViewById(R.id.mood_roster);
        socialSituation = findViewById(R.id.social_situation);
        imageUpload = findViewById(R.id.image_reason);
        reason = findViewById(R.id.reason);
        dateAndTimeMood = findViewById((R.id.date_and_time));
        submitButton = findViewById(R.id.submit_button);

        moodDate = moodEvent.getDate();
        moodTime = moodEvent.getTime();
        moodEmotionalState = moodEvent.getEmotionalState();
        moodImageUrl = moodEvent.getImageUrl();
        moodReason = moodEvent.getReason();
        moodSocialSituation = moodEvent.getSocialSituation();

        socialSituation.setText(moodSocialSituation);
        reason.setText(moodReason);
        dateAndTimeMood.setText(moodDate);
        Picasso.get().load(moodImageUrl).into(imageUpload);     // set the image according to the given URL





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

        moodRoster.setAdapter(moodRosterAdapter);

        //styling to show a glimpse of prev and next moods
        moodRoster.setClipToPadding(false);
        moodRoster.setPadding(250,0,250,0);
        moodRoster.setPageMargin(50);
        // make the moodEmotionalState the mood given by showEvent
        //moodEmotionalState = "HAPPY";
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


    }
}