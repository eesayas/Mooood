package com.example.mooood;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

/**
 * Purpose: This edits a given MoodEvent in the DB. Borrows mostly from CreateEventActivity
 */
public class EditEventActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "For Testing";
    public static final String MOOD_EVENT = "Mood Event";

    //Declare variables for later use
    ViewPager moodRoster;
    SwipeMoodsAdapter moodRosterAdapter;
    List<Emoticon> moodImages;

    //Firebase setup!
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

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
    Boolean reasonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Accessing Document
        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOOD_EVENT);

        // get the views
        getTextAndImageView();

        // get the attributes of the mood event
        getAttrMoodEvent(moodEvent);

        //set the views according to the mood event
        setProperEmoticon();

        //get the document with the same author
        documentReference = db.collection("MoodEvents").document(moodAuthor);

        //Creating a mood roster
        createMoodRoster();

        //adapter for mood roster
        moodRosterAdapter = new SwipeMoodsAdapter(moodImages, this);
        moodRoster.setAdapter(moodRosterAdapter);

        customStylingSwipeMoods();

        emoticonClickListener();

        socialSituationClickListener();


        imageUploadClickListener();

        //firebase setup
        storageReference = FirebaseStorage.getInstance().getReference("reason_image");
        databaseReference = FirebaseDatabase.getInstance().getReference("reason_image");
        dateAndTimeMood.setClickable(false);

        submitBtnClickListener(moodEvent);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inputChecker();


    } //onCreate

    /**
     * This sets the proper emoticon
     */
    private void setProperEmoticon(){
        socialSituation.setText(moodSocialSituation);
        reason.setText(moodReason);
        dateAndTimeMood.setText(moodDate +" "+ moodTime);
        if(moodImageUrl == null){
            imageUpload.setImageResource(R.drawable.temp_image_upload);
        }
        else {
            Picasso.get().load(moodImageUrl).into(imageUpload);     // set the image according to the given URL
        }
    }

    /**
     * This is the click listener for submit btn
     * **/
    private void submitBtnClickListener(final MoodEvent moodEvent){
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
                createTimeStamp();

                moodEvent.setTimeStamp(moodTimeStamp);

                EditMoodEventDB(documentReference,moodEvent);

                Intent intent = new Intent(EditEventActivity.this, UserFeedActivity.class);
                intent.putExtra("accountKey", moodAuthor);
                startActivity(intent);

            }
        });
    }
    /**
     * This checks if reason is only 3 words or 20 characters
     */

    private void inputChecker(){
        submitButton.setEnabled(false);
        if(moodDate != null && moodTime != null){
            submitButton.setEnabled(true);
        }
        final EditText reasonText = findViewById(R.id.reason);
        reasonText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                {
                    int number = countWords(s.toString());
                    if (number < 4){
                        moodReason = reasonText.getText().toString();
                        reasonCount = true;
                        submitButton.setEnabled(true);

                    }
                    else{
                        Toast.makeText(EditEventActivity.this, "reason cannot be more than 3 words!",
                                Toast.LENGTH_SHORT).show();
                        reasonCount = false;
                        submitButton.setEnabled(false);
                    }
                }

            }
        });
    }

    /**
     * This creates the timestamp needed for sorting the MoodEvent
     * **/
    private void createTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");

        try {
            moodTimeStamp = simpleDateFormat.parse(moodDate + ' ' + moodTime);

        } catch (ParseException e){
            e.printStackTrace();
        }

    }

    /**
     * This is the click listener for image upload, opens gallery
     * **/
    private void imageUploadClickListener(){
        imageUpload = findViewById(R.id.image_reason);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    /**
     * This opens fragment to select social situation
     * */
    private void socialSituationClickListener(){
        socialSituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SocialSituationFragment().show(getSupportFragmentManager(), "ADD_SOCIAL_SITUATON");
            }
        });
    }

    /**
     * This a listener for the selected Emoticon
     **/
    private void emoticonClickListener(){
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

    /**
     * This sets the proper Emoticon to reflected by the MoodEvent
     * */
    private void customStylingSwipeMoods(){
        //styling to show a glimpse of prev and next moods
        moodRoster.setClipToPadding(false);
        moodRoster.setPadding(250,0,250,0);
        moodRoster.setPageMargin(50);


        // make the moodEmotionalState the mood given by showEvent
        int pos=0;
        for(int i = 0; i < 6; i++){
            if(moodEmotionalState.equals(moodImages.get(i).getEmotionalState())){
                pos = i;
                break;
            }
            else pos = 0;
        }
        moodRoster.setCurrentItem(pos);
    }

    /**
     * Create MOOD roster of emoticons
     * */
    private void createMoodRoster(){
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
     * get values of moodEvent
     * */
    private void getAttrMoodEvent(MoodEvent moodEvent){
        moodDate = moodEvent.getDate();
        moodTime = moodEvent.getTime();
        moodEmotionalState = moodEvent.getEmotionalState();
        moodImageUrl = moodEvent.getImageUrl();
        moodReason = moodEvent.getReason();
        moodSocialSituation = moodEvent.getSocialSituation();
        moodDocID = moodEvent.getDocumentId();
        moodAuthor = moodEvent.getAuthor();
    }

    /**
     * get text view and image view
     */
    private void getTextAndImageView(){
        moodRoster = findViewById(R.id.mood_roster);
        socialSituation = findViewById(R.id.social_situation);
        imageUpload = findViewById(R.id.image_reason);
        reason = findViewById(R.id.reason);
        dateAndTimeMood = findViewById((R.id.date_and_time));
        submitButton = findViewById(R.id.submit_button);
        cancelButton = findViewById(R.id.cancel_button);
    }


    /**
     * This edits the moodEvent in DB
     * */
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


    /**
     IMAGE UPLOAD METHODS
    **/

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
    /**
     * This is needed for checking word lengths on text input fields
     **/
    public static int countWords(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        String[] words = input.split("\\s+");
        return words.length;
    }


}