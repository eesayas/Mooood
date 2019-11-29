package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Show profile of searched user and can send request
 */
public class followerActivity extends AppCompatActivity {
    private Button followButton;
    Button backButton;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private TextView setDate, setTime, setAuthor;
    private String loginName, toFollow, userState;
    ImageView recentEmoji;


    /**
     * This implements all methods below accordingly
     * Will also check to see if user has already sent a request to change text of button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_profile);

        setDate = findViewById(R.id.recentMoodDate);
        setTime = findViewById(R.id.recentMoodTime);
        setAuthor = findViewById(R.id.author);
        followButton = findViewById(R.id.follow_button);
        recentEmoji = findViewById(R.id.recentMoodEmoticon);


        collectionReference = db.collection("MoodEvents");
        Intent intent = getIntent();
        toFollow = intent.getStringExtra("accountMood");
        loginName = intent.getStringExtra("loginName");
        userState = intent.getStringExtra("emotional");

        final String date = intent.getStringExtra("moodDate");
        final String time = intent.getStringExtra("moodTime");
        final String author = intent.getStringExtra("moodAuthor");

        Log.d("follower", "date " + date);
        Log.d("follower", "time " + time);
        Log.d("follower", "Author " + author);
        setDate.setText(date);
        setTime.setText(time);
        setAuthor.setText(author);
        recentEmoji.setImageResource(new Emoticon(userState, 2).getImageLink());



        checkButtonContent();



        backToFeed();
    }



    /**
     * Will take user back to Feed Activity by clicking on the back button
     */
    private void backToFeed() {
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Will check to see content of the button, if already sent a request then Button will set to "request sent" if not it will be "follow"
     */
    private void checkButtonContent() {


        collectionReference.document(toFollow).collection("Request").document(loginName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // if Hyeon69 has a request Fahad is not following so dont need to check
                        followButton.setText(R.string.cancel_request);


                    } else {
                        collectionReference.document(loginName).collection("Following").document(toFollow)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        followButton.setText(R.string.unfollow);

                                    }
                                    else {
                                        followButton.setText(R.string.follow_user);

                                    }
                                }
                            }
                        });
                    }
                } else {
                    Log.d("checking", "Failed with: ", task.getException());
                }

            }
        });



    }


    /**
     * After clicking follow button, will send a request to the account while adding request collection to database
     * If already following, cannot send another request
     */
    private void followUser() {
        Date currentTime = Calendar.getInstance().getTime();
        //LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat requestDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");
        final String date = requestDateFormat.format(currentTime);
        Log.d("aaa",  "here" + followButton.getText().toString());

        if (followButton.getText().toString().equals("Unfollow")){
            Log.d("aaa",  "unfollowing");

            collectionReference.document(loginName).collection("Following").document(toFollow)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Unfollow", "DocumentSnapshot successfully deleted!");
                    followButton.setText(R.string.follow_user);

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Unfollow", "Error deleting document", e);
                }
            });
        }

        else if (followButton.getText().toString().equals("Follow") || followButton.getText().toString().equals("Cancel Request")) {
            final Map<String, Object> request = new HashMap<>();
            collectionReference.document(toFollow).collection("Request").document(loginName)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // hyeon69!.request.fahad.exists => request sent// cancel request
                        if (document.exists()) {
                            Log.d("Follow", "Request Sent");
                            followButton.setText(R.string.follow_user);
                            collectionReference.document(toFollow).collection("Request").document(loginName)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Unfollow", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Unfollow", "Error deleting document", e);
                                }
                            });
                            followButton.setText(R.string.follow_user);

                        } else {
                            request.put("Username", loginName);
                            request.put("Request", "Sent");
                            request.put("Request Time", date);
                            collectionReference.document(toFollow).collection("Request").document(loginName).set(request);
                            followButton.setText(R.string.cancel_request);
                            Log.d("SENT", "request sent");
                        }
                    } else {
                        Log.d("checking", "Failed with: ", task.getException());
                    }
                }

            });

        }



    }


    /**
     * calls follow user whenever the button to follow, unfollow, or cancel request is clicked
     */
    public void Click(View view) {
        Log.d("aaa", "clicked");
        followUser();
    }
}

