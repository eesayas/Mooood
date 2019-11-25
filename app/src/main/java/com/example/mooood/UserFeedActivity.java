package com.example.mooood;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.local.QueryResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * FILE PURPOSE: This is for displaying all of User's MoodEvents
 */

public class UserFeedActivity extends AppCompatActivity {

    private static final String TAG = "For Testing";
    public static final String MOOD_EVENT = "Mood Event";

    //Declare the variables for reference later
    SwipeMenuListView postList;
    ArrayAdapter<MoodEvent> postAdapter;
    ArrayList<MoodEvent> postDataList;
    SearchView userSearchView;
    Button feedButton;
    Date moodTimeStamp;


    //Firebase setup!
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private String textSubmitted;

    /**
     * This implements all methods below accordingly
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        Intent intent = getIntent();
        final String accountName = intent.getStringExtra("accountKey");
        documentReference = db.collection("MoodEvents").document(accountName);
        collectionReference = db.collection("MoodEvents").document(accountName).collection("MoodActivities");


        arrayAdapterSetup();
        createDeleteButtonOnSwipe();
        filterMood();
        deleteBtnClickListener();
        createPostBtnClickListener(accountName);
        showEventClickListener();
        selectFeed(accountName);



    } //end of onCreate

    /**
     * This will collect and show all of User's MoodEvent in DB
     */
    @Override
    protected void onStart() {
        super.onStart();
        documentReference.collection("MoodActivities")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        postDataList.clear();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String author = (String) documentSnapshot.getData().get("author");
                            String date = (String) documentSnapshot.getData().get("date");
                            String time = (String) documentSnapshot.getData().get("time");
                            String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                            String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                            String reason = (String) documentSnapshot.getData().get("reason");
                            String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                            MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                            moodEvent.setDocumentId(documentSnapshot.getId());

                            postDataList.add(moodEvent); //add to data list
                        }

                        postAdapter.notifyDataSetChanged();
                    }
                });

    } //end of OnStart()


        /**
         * This deletes a MoodEvent from the DB
         * @param documentReference
         *     This is documentReference of MoodEvent in DB
         * @param position
         *     This is the position of MoodEvent in postDataList
         */
        //delete from database
        private void deleteMoodEventFromDB (DocumentReference documentReference,int position){
            documentReference.collection("MoodActivities")
                    .document(postDataList.get(position).getDocumentId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Mood was successfully deleted");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Mood was not deleted", e);
                        }
                    });

            postDataList.remove(position);
            postAdapter.notifyDataSetChanged();
        }

        /**
         * This creates the deleteButton for SwipeMenuListView
         *
         * The MIT License (MIT)
         *
         * Copyright (c) 2014 baoyongzhang
         *
         * Permission is hereby granted, free of charge, to any person obtaining a copy
         * of this software and associated documentation files (the "Software"), to deal
         * in the Software without restriction, including without limitation the rights
         * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
         * copies of the Software, and to permit persons to whom the Software is
         * furnished to do so, subject to the following conditions:
         *
         * The above copyright notice and this permission notice shall be included in all
         * copies or substantial portions of the Software.
         *
         * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
         * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
         * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
         * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
         * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
         * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
         * SOFTWARE.
         */
        private void createDeleteButtonOnSwipe () {
            //adding delete button to SwipeMenu
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {

                    //init delete button
                    SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());

                    //custom design for delete button
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                    deleteItem.setWidth(200);
                    deleteItem.setIcon(R.drawable.ic_delete_forever);

                    //add button
                    menu.addMenuItem(deleteItem);
                }
            };

            // set creator
            postList.setMenuCreator(creator);
        }

        /**
         * This is a basic Array Adapter setup based on the lab lectures
         */

        private void arrayAdapterSetup () {
            //basic ArrayAdapter init
            postDataList = new ArrayList<>();
            postList = findViewById(R.id.posts_list);
            postAdapter = new MoodEventsAdapter(postDataList, this);
            postList.setAdapter(postAdapter);
        }

        /**
         * This is a click listener for each delete button in SwipeMenuListView
         */

        private void deleteBtnClickListener () {
            //click listener for garbage can icon
            postList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            deleteMoodEventFromDB(documentReference, position);
                            break;
                    }
                    return false;
                }
            });
        }

        /**
         * This is a click listener for create post. Will redirect to CreateEventActivity
         */
        private void createPostBtnClickListener ( final String accountName){
            final FloatingActionButton createPostBtn = findViewById(R.id.fab);
            createPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent.putExtra("key", accountName);
                    startActivity(intent);

                }
            });
        }



        /**
         * This is a click listener for show event. Will redirect to ShowEventActivity
         */
        private void showEventClickListener () {
            //click listener for each item -> ShowEventActivity
            postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(UserFeedActivity.this, ShowEventActivity.class);
                    intent.putExtra(MOOD_EVENT, postDataList.get(i));
                    startActivity(intent);

                }
            });
        }

    /**
     * This is the SearchView that will filter through Database of the user for the Mood entered and display it
     */
    private void filterMood () {
            userSearchView = findViewById(R.id.userSearchView);

            userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    textSubmitted=s.toUpperCase();
                    collectionReference
                            .orderBy("timeStamp", Query.Direction.DESCENDING)
                            .whereEqualTo("emotionalState", textSubmitted)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    postDataList.clear();
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        String author = (String) documentSnapshot.getData().get("author");
                                        String date = (String) documentSnapshot.getData().get("date");
                                        String time = (String) documentSnapshot.getData().get("time");
                                        String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                                        String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                                        String reason = (String) documentSnapshot.getData().get("reason");
                                        String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                                        MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                        moodEvent.setDocumentId(documentSnapshot.getId());
                                        postDataList.add(moodEvent); //add to data list
                                    }
                                    postAdapter.notifyDataSetChanged();
                                }
                            });
                    return false;

                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }

            });

        userSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onStart();
                return false;
            }
        });

    }

    /**
     * This will take the user from the User Activity to the Feed Activity while also updating the database
     * with the most recent mood event for all accounts
     * @param accountName
     *  This is the account name signed up with
     */
    private void selectFeed(final String accountName){
        feedButton= findViewById(R.id.feedButton);

        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("participant").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                            final String participant = doc.getId();
                            Log.d("display", participant);
                            db.collection("MoodEvents").document(participant).collection("MoodActivities")
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
                                                    Log.d("Time1", "changing timestamp in Oncreate");
                                                }catch (ParseException e){
                                                    Log.d("Time1", "catch exception in Oncreate");
                                                    e.printStackTrace();
                                                }
                                                final MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                                moodEvent.setDocumentId(documentSnapshot.getId());
                                                moodEvent.setTimeStamp(moodTimeStamp);

                                                db.collection("Users").document(participant).set(moodEvent);
                                                Log.d(TAG, "ADDED to database");

                                                //This will update the following list of the user
                                                //updateFollowingList(name, participant, moodEvent);
                                            }
                                        }
                                    });
                        }

                    }
                });

                Intent intent = new Intent(UserFeedActivity.this, feedActivity.class);
                intent.putExtra("account", accountName);
                startActivity(intent);
            }
        });
    }


}