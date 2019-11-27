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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;


public class feedActivity extends AppCompatActivity {

    private static final String TAG= "Debugging";
    public static final String MOOD_EVENT = "Mood Event";
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
    String edit;
    private String name;


    //Firebase setup
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private CollectionReference feedCollectionReference;


    private TextView userId;

    
    /**
     * This implements all methods below accordingly
     */

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
        feedDataList = new ArrayList<>();
        arrayAdapterSetup();
        followAdapter();
        searchUsers(name);
        selectUser();
        notificationCheck(name);
        showEventClickListener();
        feedDataList.clear();
        Adapter.notifyDataSetChanged();

    } //End of onCreate

    /**
     * The list of people the User is following is accessed from the database and there most recent mood event is displayed
     */
    @Override
    protected void onStart() {
        super.onStart();
        feedDataList.clear();
        Adapter.notifyDataSetChanged();
        feedCollectionReference.
                addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                            final String name = queryDocumentSnapshot.getId();
                            db.collection("MoodEvents").document(name).collection("MoodActivities")
                                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                                    .limit(1)
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
                                                if (feedDataList.contains(moodEvent)){
                                                        Log.d("duplicates", "Already exist in the list   " + moodEvent.getAuthor());
                                                }
                                                else {
                                                    Log.d("duplicates", "added in the list " + moodEvent.getAuthor());
                                                    feedDataList.add(moodEvent);
                                                }

                                            }
                                            Collections.sort(feedDataList, new Comparator<MoodEvent>() {
                                                public int compare(MoodEvent o1, MoodEvent o2) {
                                                    return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                                                }
                                            });

                                            Adapter.notifyDataSetChanged();

                                        }

                                    });
                        }
                    }
                });
    }
    private void showEventClickListener () {
        //click listener for each item -> ShowEventActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edit = "false";
                Intent intent = new Intent(feedActivity.this, ShowEventActivity.class);
                intent.putExtra(MOOD_EVENT, feedDataList.get(i));
                intent.putExtra("bool",edit);
                startActivity(intent);

            }
        });
    }

    private void notificationCheck(final String userName){
        notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(feedActivity.this, NotificationActivity.class);
                intent.putExtra("accountKey", userName);
                startActivity(intent);
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

    /**
     * Follow Adapter is the setup for the follow List View that will populate the FeedActivity with the
     *account that the User has searched up.
     */
    private void followAdapter(){
        searchUser = new ArrayList<>();
        followListview= findViewById(R.id.followListView);
        seacrhAdapter = new MoodEventsAdapter(searchUser, this);
        followListview.setAdapter(seacrhAdapter);
    }

    /**
     * SearchView for the user to search up accounts. After searching, will display the account user with there most
     * recent mood event. Can click on there mood to take you to the account/follow page
     * @param loginName
     *  This is the account name used to sign in
     */
    private void searchUsers (final String loginName) {
        feedSearchView = findViewById(R.id.feedSearchView);
        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                listView.setVisibility(View.INVISIBLE);
                followListview.setVisibility(View.VISIBLE);
                db.collection("MoodEvents").document(s)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d("readingdocument", s);
                            Log.d("readingdocument111111", document.getId());
                            if (document.getId().equals(s)) {
                                db.collection("MoodEvents").document(s).collection("MoodActivities")
                                        .orderBy("timeStamp", Query.Direction.DESCENDING)
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
                                                        moodTimeStamp = simpleDateFormat.parse(date + ' ' + time);
                                                        Log.d("Time1", "changing timestamp in SearchUsers");
                                                    } catch (ParseException e) {
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
                            } else {
                                Log.d("documentexist", "not exist");
                            }

                        } else {
                            Log.d("checking", "Failed with: ", task.getException());
                        }
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
                return false;
            }
        });
        //Will not allow you go to the follow activity if already following
        followListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                collectionReference.document(loginName).collection("Following").document(searchUser.get(i).getAuthor())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(feedActivity.this, "Already Following!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(feedActivity.this, followerActivity.class);
                                Log.d("followerActivity", "creating the intent");
                                intent.putExtra("accountMood", searchUser.get(i).getAuthor());
                                intent.putExtra("loginName", loginName);
                                intent.putExtra("moodDate", searchUser.get(i).getDate());
                                intent.putExtra("moodTime", searchUser.get(i).getTime());
                                intent.putExtra("moodAuthor", searchUser.get(i).getAuthor());
                                Log.d("follower", "date "+ searchUser.get(i).getDate());
                                startActivity(intent);
                            }
                        } else {
                            Log.d("checking", "Failed with: ", task.getException());
                        }
                    }
                });

            }
        });
    }

    /**
     * Clicking on User Button will simply take you back to User Activity
     */
    private void selectUser(){
        userButton= findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedDataList.clear();
                Adapter.notifyDataSetChanged();
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