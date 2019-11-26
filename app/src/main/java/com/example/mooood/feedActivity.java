package com.example.mooood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

//

public class feedActivity extends AppCompatActivity {

    private static final String TAG= "feedActivity";

// =========================BEFORE==============================
//
//    ListView listView;
//    ListView followListview;
//    ArrayAdapter<MoodEvent> Adapter;
//    ArrayAdapter<MoodEvent> seacrhAdapter;
//
//=========================AFTER================================

    RecyclerView moodEventList, searchResultList;
    MoodEventsAdapter moodEventAdapter, searchResultAdapter;

//===========================end================================

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

    //for RecyclerView
    private RecyclerTouchListener searchResultTouchListener;

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
        searchResultListener(name);

        searchUsers();
        selectUser();
        notificationCheck(name);



    } //End of onCreate

    @Override
    protected void onStart() {
        super.onStart();
        feedDataList.clear();
        moodEventAdapter.notifyDataSetChanged();
        feedCollectionReference

                .orderBy("timestamp", Query.Direction.DESCENDING) //added by eesayas because the sorting below has cannot be resolved

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

                                                String author = (String)documentSnapshot.getData().get("author");
                                                String date = (String)documentSnapshot.getData().get("date");
                                                String time = (String)documentSnapshot.getData().get("time");
                                                String emotionalState = (String)documentSnapshot.getData().get("emotionalState");
                                                String imageURl = (String)documentSnapshot.getData().get("imageUrl");
                                                String reason = (String)documentSnapshot.getData().get("reason");
                                                String socialSituation = (String)documentSnapshot.getData().get("socialSituation");
                                                String latitude = (String) documentSnapshot.getData().get("latitude");
                                                String longitude= (String) documentSnapshot.getData().get("longitude");
                                                String address = (String) documentSnapshot.getData().get("address") ;
                                                MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation,latitude,longitude,address);
                                                moodEvent.setDocumentId(documentSnapshot.getId());


                                                try {
                                                    moodTimeStamp = simpleDateFormat.parse(date + ' '+ time);
                                                    Log.d("Time1", "changing timestamp in SearchUsers");
                                                }catch (ParseException e){
                                                    Log.d("Time1", "catch exception in searchUsers");
                                                    e.printStackTrace();
                                                }

                                                moodEvent.setTimeStamp(moodTimeStamp);
                                                feedDataList.add(moodEvent); //add to data list
                                            }

                                            //NOTE: cannot resolve because of RecyclerView change
//                                            Adapter.sort(new Comparator<MoodEvent>() {
//                                                @Override
//                                                public int compare(MoodEvent moodEvent, MoodEvent t1) {
//                                                    return moodEvent.getTimeStamp().compareTo(t1.getTimeStamp());
//                                                }
//                                            });

                                            moodEventAdapter.notifyDataSetChanged();
                                        }
                                    });

                        }

                    }
                });
    }

    @Override
    public void onResume(){
        super.onResume();
        searchResultList.addOnItemTouchListener(searchResultTouchListener);
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
        moodEventList = findViewById(R.id.feedListView);
        moodEventAdapter = new MoodEventsAdapter(feedDataList);

       /* Adapter.sort(new Comparator<MoodEvent>() {
            @Override
            public int compare(MoodEvent moodEvent, MoodEvent t1) {
                return moodEvent.getTimeStamp().compareTo(t1.getTimeStamp());
            }
        });*/

        moodEventList.setLayoutManager(new LinearLayoutManager(this));
        moodEventList.setAdapter(moodEventAdapter);
    }

    /**
     * This is the setup for the list of searched Users (other users to potentially follow)
     */
    private void followAdapter(){
        searchUser = new ArrayList<>();
        searchResultList = findViewById(R.id.followListView);
        searchResultAdapter = new MoodEventsAdapter(searchUser);

        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        searchResultList.setAdapter(searchResultAdapter);
    }

    /**
     * This fetches a list of Users from DB according to search input
     * The username of the currently logged in user
     */
    private void searchUsers () {

        feedSearchView = findViewById(R.id.feedSearchView);
        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                moodEventList.setVisibility(View.INVISIBLE); //hide recent mood event list
                searchResultList.setVisibility(View.VISIBLE); //show search results for other users
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

                                    String author = (String)documentSnapshot.getData().get("author");
                                    String date = (String)documentSnapshot.getData().get("date");
                                    String time = (String)documentSnapshot.getData().get("time");
                                    String emotionalState = (String)documentSnapshot.getData().get("emotionalState");
                                    String imageURl = (String)documentSnapshot.getData().get("imageUrl");
                                    String reason = (String)documentSnapshot.getData().get("reason");
                                    String socialSituation = (String)documentSnapshot.getData().get("socialSituation");
                                    String latitude = (String) documentSnapshot.getData().get("latitude");
                                    String longitude= (String) documentSnapshot.getData().get("longitude");
                                    String address = (String) documentSnapshot.getData().get("address") ;
                                    MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation,latitude,longitude,address);
                                    moodEvent.setDocumentId(documentSnapshot.getId());


                                    try {
                                        moodTimeStamp = simpleDateFormat.parse(date + ' '+ time);
                                        Log.d("Time1", "changing timestamp in SearchUsers");
                                    }catch (ParseException e){
                                        Log.d("Time1", "catch exception in searchUsers");
                                        e.printStackTrace();
                                    }

                                    moodEvent.setTimeStamp(moodTimeStamp);

                                    searchUser.add(moodEvent); //add to data list

                                }
                                searchResultAdapter.notifyDataSetChanged();
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
                searchResultList.setVisibility(View.INVISIBLE);
                moodEventList.setVisibility(View.VISIBLE); //show recent mood events again
                onStart();
                return false;
            }
        });


        //NOTE: implementation will be diff because of RecyclerView change
//        followListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(feedActivity.this, followerActivity.class);
//                intent.putExtra("accountMood", searchUser.get(i).getAuthor());
//                intent.putExtra("loginName", loginName);
//                intent.putExtra("mood", searchUser.get(i));
//                startActivity(intent);
//
//            }
//        });
    }

    /**
     * This setups the click listener for each item in searchResultList [adhering RecyclerView]
     * @param loginName
     * The username of the currently logged in user
     */
    private void searchResultListener(final String loginName){
        searchResultTouchListener = new RecyclerTouchListener(this, searchResultList);
        searchResultTouchListener.setClickable(new RecyclerTouchListener.OnRowClickListener() {
            @Override
            public void onRowClicked(int position) {
                Log.d(TAG, "Traveling to followerActivity");
                goToFollowerActivity(loginName, position);

            }

            @Override
            public void onIndependentViewClicked(int independentViewID, int position) {

            }
        });

    }

    /**
     * This redirects to followerActivity which shows the chosen User's profile?
     * @param loginName
     * The username of the currently logged in user
     *
     */
    private void goToFollowerActivity(final String loginName, int i){

        Intent intent = new Intent(feedActivity.this, followerActivity.class);
                intent.putExtra("accountMood", searchUser.get(i).getAuthor());
                intent.putExtra("loginName", loginName);
                intent.putExtra("mood", searchUser.get(i));
                startActivity(intent);
    }

    /**
     * This redirects back to the current logged in User's MoodEvent list
     */
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