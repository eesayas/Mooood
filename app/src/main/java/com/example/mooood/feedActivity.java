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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class feedActivity extends AppCompatActivity {

    private static final String TAG= "Debugging";
    ListView listView;
    ArrayAdapter<MoodEvent> Adapter;
    ArrayList<MoodEvent> feedDataList;
    SearchView feedSearchView;
    ArrayList<String> usernames;
    FloatingActionButton notificationButton;
    Button userButton;

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
        feedDataList = new ArrayList<>();
        usernames= new ArrayList<>();

        db.collection("participant").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                //usernames.clear();
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    usernames.add((String)doc.getId());
                    //Log.d("USERNAME", names);
                    //usernames.add(names);
                }

            }
        });

        Log.d("ADDUSERS", "Working before loop" + usernames.size());

        for (int i = 0; i < usernames.size(); i++) {
            Log.d("ADDUSERS", "Working after loop");

            final int finalI = i;
            collectionReference.document(usernames.get(i)).collection("MoodActivities")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                //Date timeStamp = (Date) documentSnapshot.getData().get("timeStamp");
                                String author = (String) documentSnapshot.getData().get("author");
                                String date = (String) documentSnapshot.getData().get("date");
                                String time = (String) documentSnapshot.getData().get("time");
                                String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                                String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                                String reason = (String) documentSnapshot.getData().get("reason");
                                String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                                MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                moodEvent.setDocumentId(documentSnapshot.getId());
                                //moodEvent.setTimeStamp(timeStamp);

                                db.collection("Users").document(usernames.get(finalI)).set(moodEvent);
                                Log.d(TAG, "ADDED to database");
                            }

                        }
                    });
        }

        arrayAdapterSetup();
        //addUsers();
        searchUsers(name);
        selectUser(name);



    } //End of onCreate

 @Override
    protected void onStart() {
        super.onStart();
        //Displaying most recent Mood events that the User is following
             feedCollectionReference
             .orderBy("timeStamp", Query.Direction.DESCENDING)
             .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                 @Override
                 public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                     feedDataList.clear();

                     for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                         //Date timeStamp = (Date) documentSnapshot.getData().get("timeStamp");
                         String author = (String) documentSnapshot.getData().get("author");
                         String date = (String) documentSnapshot.getData().get("date");
                         String time = (String) documentSnapshot.getData().get("time");
                         String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                         String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                         String reason = (String) documentSnapshot.getData().get("reason");
                         String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                         MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                         moodEvent.setDocumentId(documentSnapshot.getId());
                         //moodEvent.setTimeStamp(timeStamp);

                         feedDataList.add(moodEvent); //add to data list
                     }

                     Adapter.notifyDataSetChanged();
                 }
             });
    }
    private void arrayAdapterSetup () {
        //basic ArrayAdapter init

        listView = findViewById(R.id.feedListView);
        Adapter = new MoodEventsAdapter(feedDataList, this);
        listView.setAdapter(Adapter);
    }


   /* private void addUsers(){
        //From the userNames of Users in the MainActivity, go through all the users and collect the most recent moodEvent.
        // Then form a new collection with it
        *//*Log.d("ADDUSERS", "Working before loop");
        for (int i = 0; i < usernames.size(); i++) {
            Log.d("ADDUSERS", "Working after loop");

            final int finalI = i;
            collectionReference.document(usernames.get(i)).collection("MoodActivities")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                //Date timeStamp = (Date) documentSnapshot.getData().get("timeStamp");
                                String author = (String) documentSnapshot.getData().get("author");
                                String date = (String) documentSnapshot.getData().get("date");
                                String time = (String) documentSnapshot.getData().get("time");
                                String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                                String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                                String reason = (String) documentSnapshot.getData().get("reason");
                                String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                                Date timeStamp = (Date) documentSnapshot.getData().get("timeStamp");
                                MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                moodEvent.setDocumentId(documentSnapshot.getId());
                                //moodEvent.setTimeStamp(timeStamp);

                                db.collection("Users").document(usernames.get(finalI)).set(moodEvent);
                                Log.d(TAG, "ADDED to database");
                            }

                        }
                    });
        }*//*
    }*/

    private void searchUsers (final String name) {


        feedSearchView = findViewById(R.id.feedSearchView);
        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Will search through the collection "Users" and try to match the Name submitted in the searchView with the name in the database
                // If it is matched, the name searched up will display the user's most recent mood

                db.collection("Users")
                        .whereEqualTo("author", s)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                feedDataList.clear();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    //Date timeStamp = (Date) documentSnapshot.getData().get("timeStamp");
                                    String author = (String) documentSnapshot.getData().get("author");
                                    String date = (String) documentSnapshot.getData().get("date");
                                    String time = (String) documentSnapshot.getData().get("time");
                                    String emotionalState = (String) documentSnapshot.getData().get("emotionalState");
                                    String imageURl = (String) documentSnapshot.getData().get("imageUrl");
                                    String reason = (String) documentSnapshot.getData().get("reason");
                                    String socialSituation = (String) documentSnapshot.getData().get("socialSituation");
                                    MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation);
                                    moodEvent.setDocumentId(documentSnapshot.getId());
                                    //moodEvent.setTimeStamp(timeStamp);

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
                //onStart();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //When the mood of the searched name is clicked, it will show a Toast message that you are "Now Following"
                //this person and will add the name and there most recent mood under the collection "Following"
                //This will not be how the User follows people though!

                MoodEvent mood =feedDataList.get(i);
                Toast.makeText(getApplicationContext(), "NOW FOLLOWING", Toast.LENGTH_SHORT).show();
                db.collection("MoodEvents").document(name).collection("Following")
                        .document(mood.getAuthor()).set(mood);
            }
        });
    }
    private void selectUser(final String accountName){
        userButton= findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(feedActivity.this, UserFeedActivity.class);
                intent.putExtra("accountKey", accountName);
                startActivity(intent);
            }
        });
    }

}
