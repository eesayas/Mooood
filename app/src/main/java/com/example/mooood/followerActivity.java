package com.example.mooood;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class followerActivity extends AppCompatActivity {
    Button followButton;
    Button backButton;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    TextView setDate;
    TextView setTime;
    TextView setAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_profile);

        setDate=findViewById(R.id.date);
        setTime = findViewById(R.id.time);
        setAuthor= findViewById(R.id.author);
        collectionReference=db.collection("MoodEvents");
        Intent intent = getIntent();
        final String toFollow = intent.getStringExtra("accountMood");
        final String loginName= intent.getStringExtra("loginName");
        final MoodEvent moodEvent = intent.getParcelableExtra("mood");

        setDate.setText(moodEvent.getDate());
        setTime.setText(moodEvent.getTime());
        setAuthor.setText(moodEvent.getAuthor());

       /* collectionReference.document(toFollow).collection("Request")
                .whereEqualTo("Username", loginName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        button.setText("REQUEST SENT");
                    }
                });*/
        followUser(toFollow, loginName);
        backToFeed();
    }//End of onCreate

    private void followUser(final String toFollow, final String loginName) {
        followButton = findViewById(R.id.follow_button);
        Date currentTime = Calendar.getInstance().getTime();
        //LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat requestDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");
        String date = requestDateFormat.format(currentTime);

        final Map<String, Object> request = new HashMap<>();
        request.put("Username",loginName);
        request.put("Request", "Sent");
        request.put("Request Time", date);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("login name after", loginName);
                Log.d("trying to follow after", toFollow);
                collectionReference.document(toFollow).collection("Request").document(loginName).set(request);
                followButton.setText("REQUEST SENT");
                Log.d("SENT", "request sent");

            }
        });
    }

    private void backToFeed(){
        backButton= findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             finish();
            }
        });
    }
}
