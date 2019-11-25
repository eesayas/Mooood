package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class feedActivity extends AppCompatActivity {

    private static final String TAG= "Debugging";
    ListView listView;
    ArrayAdapter<MoodEvent> Adapter;
    ArrayList<MoodEvent> feedDataList;
    SearchView feedSearchView;
    FloatingActionButton notificationButton;
    Button userButton;
    Date moodTimeStamp;

    private String name;


    //Firebase setup
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private CollectionReference feedCollectionReference;

    private TextView userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Intent intent = getIntent();
        name = intent.getStringExtra("account");

        userId = findViewById(R.id.activity_user_feed_tv_id);
        userId.setText(name);

        feedCollectionReference = db.collection("MoodEvents").document(name).collection("Following");
        collectionReference = db.collection("MoodEvents");

        arrayAdapterSetup();
        feedDataList.clear();
        Adapter.notifyDataSetChanged();
        searchUsers(name);
        selectUser();

    } //End of onCreate

    @Override
    protected void onStart() {
        super.onStart();
        feedDataList.clear();
        Adapter.notifyDataSetChanged();
        feedCollectionReference
                //.orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        feedDataList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            final String followers = documentSnapshot.getId();
                            db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        String users= documentSnapshot.getId();
                                        if(followers.equals(users)){
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");
                                            String author = (String) documentSnapshot.getData().get("author");
                                            String date = (String) documentSnapshot.getData().get("date");
                                            String time = (String) documentSnapshot.getData().get("time");
                                            String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                                            String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                                            String reason = (String) documentSnapshot.getData().get("reason");
                                            String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                                            try {
                                                moodTimeStamp = simpleDateFormat.parse(date + ' '+ time);
                                                Log.d("Time1", "changing timestamp in OnStart");
                                            }catch (ParseException a){
                                                Log.d("Time1", "catch exception in Onstart");
                                                a.printStackTrace();
                                            }
                                            MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                            moodEvent.setDocumentId(documentSnapshot.getId());
                                            moodEvent.setTimeStamp(moodTimeStamp);

                                            feedDataList.add(moodEvent); //add to data list
                                        }
                                        Adapter.notifyDataSetChanged();

                                    }
                                }
                            });

                        }

                    }
                });
    }

    private void arrayAdapterSetup () {
        //basic ArrayAdapter init
        feedDataList = new ArrayList<>();
        listView = findViewById(R.id.feedListView);
        Adapter = new MoodEventsAdapter(feedDataList, this);
        listView.setAdapter(Adapter);
    }

    private void searchUsers (final String name) {
        feedSearchView = findViewById(R.id.feedSearchView);
        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                db.collection("Users")
                        .whereEqualTo("author", s)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                feedDataList.clear();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");

                                    String author = (String) documentSnapshot.getData().get("author");
                                    String date = (String) documentSnapshot.getData().get("date");
                                    String time = (String) documentSnapshot.getData().get("time");
                                    String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                                    String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                                    String reason = (String) documentSnapshot.getData().get("reason");
                                    String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                                    try {
                                        moodTimeStamp = simpleDateFormat.parse(date + ' '+ time);
                                        Log.d("Time1", "changing timestamp in SearchUsers");
                                    }catch (ParseException e){
                                        Log.d("Time1", "catch exception in searchUsers");
                                        e.printStackTrace();
                                    }
                                    MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                    moodEvent.setDocumentId(documentSnapshot.getId());
                                    moodEvent.setTimeStamp(moodTimeStamp);

                                    feedDataList.add(moodEvent); //add to data list
                                }
                                Adapter.notifyDataSetChanged();
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

        feedSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onStart();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(feedActivity.this, followerActivity.class);
                intent.putExtra("accountMood", feedDataList.get(i));
                startActivity(intent);

            }
        });
    }

    private void selectUser(){
        userButton= findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * This is a click listener to go to profile
     */
    public void goToProfile(View view) {

        db.collection("Users")
                .whereEqualTo("author", name)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        feedDataList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");

                            String author = (String) documentSnapshot.getData().get("author");
                            String date = (String) documentSnapshot.getData().get("date");
                            String time = (String) documentSnapshot.getData().get("time");
                            String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                            String reason = (String) documentSnapshot.getData().get("reason");
                            String socialSituation = (String) documentSnapshot.getData().get("socialSituation");


                            Intent intent = new Intent(feedActivity.this, UserProfile.class);

                            intent.putExtra("AUTHOR", author);
                            intent.putExtra("DATE", date);
                            intent.putExtra("TIME", time);
                            intent.putExtra("STATE", emotionalState);
                            intent.putExtra("REASON", reason);
                            intent.putExtra("SITUATION", socialSituation);
                            startActivity(intent);
                        }

                    }
                });
    }



}