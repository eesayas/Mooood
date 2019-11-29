package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class feedActivity extends AppCompatActivity {

    private static final String TAG= "feedActivity";
    public static final String MOOD_EVENT = "Mood Event";

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
    ImageButton mapButton;
    Date moodTimeStamp;
    String edit;
    private String name;
    TextView userProfile;

    //Firebase setup
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private CollectionReference feedCollectionReference;


    //for RecyclerView
    private RecyclerTouchListener searchResultTouchListener;
    private RecyclerTouchListener recentMoodsTouchListener;

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
        searchResultListener(name);
        recentMoodsListener();


        searchUsers(name);
        selectUser();
        openMoodMap();
        notificationCheck(name);
        goToProfile();
        feedDataList.clear();
        moodEventAdapter.notifyDataSetChanged();


    } //End of onCreate

    /**
     * The list of people the User is following is accessed from the database and there most recent mood event is displayed
     */
    @Override
    protected void onStart() {
        super.onStart();
        feedDataList.clear();
        moodEventAdapter.notifyDataSetChanged();
        feedCollectionReference
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        feedDataList.clear();
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
        moodEventList.addOnItemTouchListener(recentMoodsTouchListener);
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
                moodEventList.setVisibility(View.INVISIBLE);
                searchResultList.setVisibility(View.VISIBLE);
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
                                                        moodTimeStamp = simpleDateFormat.parse(date + ' ' + time);
                                                        Log.d("Time1", "changing timestamp in SearchUsers");
                                                    } catch (ParseException e) {
                                                        Log.d("Time1", "catch exception in searchUsers");
                                                        e.printStackTrace();
                                                    }

                                                    moodEvent.setTimeStamp(moodTimeStamp);
                                                    searchUser.add(moodEvent); //add to data list
                                                }


                                                Collections.sort(feedDataList, new Comparator<MoodEvent>() {
                                                    public int compare(MoodEvent o1, MoodEvent o2) {
                                                        return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                                                    }
                                                });

                                                searchResultAdapter.notifyDataSetChanged();
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
                searchResultList.setVisibility(View.INVISIBLE);
                moodEventList.setVisibility(View.VISIBLE); //show recent mood events again
                onStart();
                return false;
            }
        });


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

        //create a line that separates all MoodEvent inside the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(searchResultList.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.recyclerview_divider));
        moodEventList.addItemDecoration(dividerItemDecoration);


    }

    /**
     */
    private void recentMoodsListener(){
        recentMoodsTouchListener = new RecyclerTouchListener(this, moodEventList);
        recentMoodsTouchListener.setClickable(new RecyclerTouchListener.OnRowClickListener() {
            @Override
            public void onRowClicked(int position) {
                Log.d(TAG, "Traveling to showEventActivity");
                goToShowEventActivity(position);

            }

            @Override
            public void onIndependentViewClicked(int independentViewID, int position) {
            }
        });

        //create a line that separates all MoodEvent inside the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(moodEventList.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.recyclerview_divider));
        moodEventList.addItemDecoration(dividerItemDecoration);


    }

    private void goToShowEventActivity (int i) {

        edit = "false";
        Intent intent = new Intent(feedActivity.this, ShowEventActivity.class);
        intent.putExtra(MOOD_EVENT, feedDataList.get(i));
        intent.putExtra("bool",edit);
        startActivity(intent);
    }

    /**
     * This redirects to followerActivity which shows the chosen User's profile?
     * @param loginName
     * The username of the currently logged in user
     *
     */
    private void goToFollowerActivity(final String loginName, final int i){

        Intent intent = new Intent(feedActivity.this, followerActivity.class);
        Log.d("followerActivity", "creating the intent");
        intent.putExtra("emotional", searchUser.get(i).getEmotionalState());
        intent.putExtra("accountMood", searchUser.get(i).getAuthor());
        intent.putExtra("loginName", loginName);
        intent.putExtra("moodDate", searchUser.get(i).getDate());
        intent.putExtra("moodTime", searchUser.get(i).getTime());
        intent.putExtra("moodAuthor", searchUser.get(i).getAuthor());
        Log.d("follower", "date "+ searchUser.get(i).getDate());
        startActivity(intent);

    }


    //TODO: Fix bug where the app crashes when the UserFeedMap activity is started.
    private void openMoodMap(){
        mapButton=findViewById(R.id.map_feed_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(feedActivity.this, MoodsMapActivity.class);
                if(feedDataList.size()>0){
                    intent.putParcelableArrayListExtra("moodList",feedDataList);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(feedActivity.this,"You're not following anyone!",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Clicking on User Button will simply take you back to User Activity
     **/
    private void selectUser(){
        userButton= findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedDataList.clear();
                searchResultAdapter.notifyDataSetChanged();
                finish();
            }
        });
    }

    /**
     * This is a click listener to go to profile
     */
    public void goToProfile(){
        userProfile= findViewById(R.id.activity_feed_show_profile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.document(name).collection("MoodActivities")
                        .orderBy("timeStamp", Query.Direction.DESCENDING)
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

        });

    }






}