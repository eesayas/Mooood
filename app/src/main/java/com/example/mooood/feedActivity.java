package com.example.mooood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class feedActivity extends AppCompatActivity {

    private static final String TAG= "Debugging";
    ListView listView;
    ListView followListview;
    ArrayAdapter<MoodEvent> Adapter;
    ArrayAdapter<MoodEvent> seacrhAdapter;
    ArrayList<MoodEvent> searchUser;
    ArrayList<MoodEvent> feedDataList;
    SearchView feedSearchView;
    FloatingActionButton notificationButton;
    Button userButton;
    Date moodTimeStamp;


    //Firebase setup
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private CollectionReference feedCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("account");

        feedCollectionReference = db.collection("MoodEvents").document(name).collection("Following");
        collectionReference = db.collection("MoodEvents");

        arrayAdapterSetup();
        followAdapter();
        searchUsers(name);
        selectUser();


    } //End of onCreate

    @Override
    protected void onStart() {
        super.onStart();
        feedDataList.clear();
        Adapter.notifyDataSetChanged();
        feedCollectionReference
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            final String followers = documentSnapshot.getId();
                            Log.d("Nameoffollowers", followers);
                            db.collection("Users")
                                    .whereEqualTo("author", followers)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                            Adapter.sort(new Comparator<MoodEvent>() {
                                                @Override
                                                public int compare(MoodEvent moodEvent, MoodEvent t1) {
                                                    return moodEvent.getTimeStamp().compareTo(t1.getTimeStamp());
                                                }
                                            });
                                            Adapter.notifyDataSetChanged();
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
       /* Adapter.sort(new Comparator<MoodEvent>() {
            @Override
            public int compare(MoodEvent moodEvent, MoodEvent t1) {
                return moodEvent.getTimeStamp().compareTo(t1.getTimeStamp());
            }
        });*/
        listView.setAdapter(Adapter);
    }



    private void followAdapter(){
        searchUser = new ArrayList<>();
        followListview= findViewById(R.id.followListView);
        seacrhAdapter = new MoodEventsAdapter(searchUser, this);
        followListview.setAdapter(seacrhAdapter);
    }


    private void searchUsers (final String loginName) {

        feedSearchView = findViewById(R.id.feedSearchView);
        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                listView.setVisibility(View.INVISIBLE);
                followListview.setVisibility(View.VISIBLE);
                db.collection("Users")
                        .whereEqualTo("author", s)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                searchUser.clear();
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

                                    searchUser.add(moodEvent); //add to data list

                                }
                                seacrhAdapter.notifyDataSetChanged();
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
                followListview.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                onStart();
                return false;
            }
        });
        followListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(feedActivity.this, followerActivity.class);
                intent.putExtra("accountMood", searchUser.get(i).getAuthor());
                intent.putExtra("loginName", loginName);
                intent.putExtra("mood", searchUser.get(i));
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


}