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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    Button cancelButton;

    //needed for creating MoodEvent later
    String moodDate;
    String moodTime;
    String moodEmotionalState;
    String moodImageUrl;
    String moodReason;
    String moodSocialSituation;
    String moodDocID;
    String moodAuthor;
    Date moodTimeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Accessing Document
        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOODEVENT);

        // get the views
        moodRoster = findViewById(R.id.mood_roster);
        socialSituation = findViewById(R.id.social_situation);
        imageUpload = findViewById(R.id.image_reason);
        reason = findViewById(R.id.reason);
        dateAndTimeMood = findViewById((R.id.date_and_time));
        submitButton = findViewById(R.id.submit_button);
        cancelButton = findViewById(R.id.cancel_button);

        // get the attributes of the mood event
        moodDate = moodEvent.getDate();
        moodTime = moodEvent.getTime();
        moodEmotionalState = moodEvent.getEmotionalState();
        moodImageUrl = moodEvent.getImageUrl();
        moodReason = moodEvent.getReason();
        moodSocialSituation = moodEvent.getSocialSituation();
        moodDocID = moodEvent.getDocumentId();
        moodAuthor = moodEvent.getAuthor();

        //set the views according to the mood event
        socialSituation.setText(moodSocialSituation);
        reason.setText(moodReason);
        dateAndTimeMood.setText(moodDate +" "+ moodTime);
        Picasso.get().load(moodImageUrl).into(imageUpload);     // set the image according to the given URL

        documentReference = db.collection("MoodEvents").document(moodAuthor);       //get the document with the same author



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
        moodEmotionalState = moodEvent.getEmotionalState();
        int pos=0;
        for(int i = 0; i < 6; i++){
            if(moodEmotionalState.equals(moodImages.get(i).getEmotionalState())){
                pos = i;
                break;
            }
            else pos = 0;
        }
        moodRoster.setCurrentItem(pos);


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

        socialSituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SocialSituationFragment().show(getSupportFragmentManager(), "ADD_SOCIAL_SITUATON");
            }
        });

        //==============================================================================================
        // DATE AND TIME PICKER DIALOG FRAGMENT click listener
        //==============================================================================================

        //access date and time picker fragments
        //Resource: https://github.com/Kiarasht/Android-Templates/tree/master/Templates/DatePickerDialog
        simpleDateFormat = new SimpleDateFormat("MMM/dd/yyyy h:mm a", Locale.getDefault());
        dateAndTimeMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                new DatePickerDialog(EditEventActivity.this, DateDataSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
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


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodReason = reason.getText().toString();
                moodSocialSituation = socialSituation.getText().toString();

                moodEvent.setEmotionalState(moodEmotionalState);
                moodEvent.setDate(moodDate);
                moodEvent.setTime(moodTime);
                moodEvent.setReason(moodReason);
                moodEvent.setSocialSituation(moodSocialSituation);
                //upload image
                if(uploadTask != null && uploadTask.isInProgress()){
                    Log.d(TAG, "uploading in progress");
                } else{
                    uploadImage();
                }
                moodEvent.setImageUrl(moodImageUrl);
                //create timestamp
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm:ss a");

                try {
                    moodTimeStamp = simpleDateFormat.parse(moodDate + ' ' + moodTime);

                } catch (ParseException e){
                    e.printStackTrace();
                }
                moodEvent.setTimeStamp(moodTimeStamp);

                Log.d(TAG, "changed Url in submit");
                EditMoodEventDB(documentReference,moodEvent);

                Intent intent = new Intent(EditEventActivity.this, UserFeedActivity.class);
                intent.putExtra("accountKey", moodAuthor);
                startActivity(intent);

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    private final DatePickerDialog.OnDateSetListener DateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //get Date
            moodDate = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(calendar.getTime());

            new TimePickerDialog(EditEventActivity.this, TimeDataSet, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
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

    public void EditMoodEventDB(DocumentReference documentReference, MoodEvent mood){
        documentReference.collection("MoodActivities")
                .document(mood.getDocumentId())
                .set(mood)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Mood was successfully Edited");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Mood was not Edited", e);
                    }
                });
    }


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
            uploadImage();
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
                    Log.d(TAG, "image url changed");
                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(uploadImage);

                    //submit to db
//                    submitMoodEventToDB();


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

}