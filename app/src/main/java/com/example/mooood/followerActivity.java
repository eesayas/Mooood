package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


/**
 * Show profile of searched user and can send request
 */
public class followerActivity extends AppCompatActivity {
    private Button followButton;
    Button backButton;
    private int flag;
    private int btnFlag;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    TextView setDate;
    TextView setTime;
    TextView setAuthor;
    String loginName;
    String toFollow;

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

        collectionReference = db.collection("MoodEvents");
        Intent intent = getIntent();
        toFollow = intent.getStringExtra("accountMood");
        loginName = intent.getStringExtra("loginName");
        final String date = intent.getStringExtra("moodDate");
        final String time = intent.getStringExtra("moodTime");
        final String author = intent.getStringExtra("moodAuthor");

        Log.d("follower", "date " + date);
        Log.d("follower", "time " + time);
        Log.d("follower", "Author " + author);
        setDate.setText(date);
        setTime.setText(time);
        setAuthor.setText(author);


        btnFlag = checkButtonContent();
        followUser(btnFlag);
        backToFeed();
    }//End of onCreate

    /**
     * After clicking follow button, will send a request to the account while adding request collection to database
     * If already following, cannot send another request
     */
    private void followUser(int btnFlag) {
        Log.d("aaa", "In followUser; flag =" + btnFlag);

        Date currentTime = Calendar.getInstance().getTime();
        //LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat requestDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");
        final String date = requestDateFormat.format(currentTime);


        if (flag == 1){
            collectionReference.document(loginName).collection("Following").document(toFollow)
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

        }
        final Map<String, Object> request = new HashMap<>();
        collectionReference.document(toFollow).collection("Request").document(loginName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Follow", "Already following");

                    } else {
                        followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                request.put("Username", loginName);
                                request.put("Request", "Sent");
                                request.put("Request Time", date);
                                collectionReference.document(toFollow).collection("Request").document(loginName).set(request);
                                followButton.setText("REQUEST SENT");
                                Log.d("SENT", "request sent");
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
    private int checkButtonContent() {

        /*
        flag = 1; unfollow
        flag = 2; follow
        flag = 3; request sent
         */


        /*
         if !hyeon69.Request.Fahad && Fahad.Following.Hyeon => Unfollow
         if !hyeon69.Request.Fahad && !Fahad.Following.Hyeon => Follow
         if hyeon69.Request.Fahad && !Fahad.Following.Hyeon => Request Sent => onlick =>Cancel Req

         if
        */
        collectionReference.document(toFollow).collection("Request").document(loginName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // if Hyeon69 has a request Fahad is not following so dont need to check
                        followButton.setText(R.string.request_sent_follower);
                        flag = 3;

                    } else {
                        collectionReference.document(loginName).collection("Following").document(toFollow)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        followButton.setText(R.string.unfollow);
                                        flag = 1;
                                        Log.d("aaa", "button text => " + flag );


                                    }
                                    else {
                                        followButton.setText(R.string.follow_user);
                                        flag =2;
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


        Log.d("aaa", "done setting up button text => " + flag );

    return flag;
    }
}

