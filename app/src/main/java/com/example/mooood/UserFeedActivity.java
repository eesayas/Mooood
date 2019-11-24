package com.example.mooood;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

/**
 * FILE PURPOSE: This is for displaying all of User's MoodEvents
 */

public class UserFeedActivity extends AppCompatActivity implements MoodEventsAdapter.OnMoodEventListener {

    private static final String TAG = "For Testing";
    public static final String MOOD_EVENT = "Mood Event";

    //Declare the variables for reference later
    RecyclerView postList;
    ArrayList<MoodEvent> postDataList;
    private MoodEventsAdapter postAdapter;

    //Firebase setup!
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

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

        createPostBtnClickListener(accountName);
//        showEventClickListener();
        moodEventAdapterSetup();
        swipeControllerSetup();

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

                                String author = (String)documentSnapshot.getData().get("author");
                                String date = (String)documentSnapshot.getData().get("date");
                                String time = (String)documentSnapshot.getData().get("time");
                                String emotionalState = (String)documentSnapshot.getData().get("emotionalState");
                                String imageURl = (String)documentSnapshot.getData().get("imageUrl");
                                String reason = (String)documentSnapshot.getData().get("reason");
                                String socialSituation = (String)documentSnapshot.getData().get("socialSituation");
                                String latitude = (String) documentSnapshot.getData().get("latitude");
                                String longitude= (String) documentSnapshot.getData().get("longitude");
                                MoodEvent moodEvent = new MoodEvent(author, date, time, emotionalState, imageURl, reason, socialSituation,latitude,longitude);
                                moodEvent.setDocumentId(documentSnapshot.getId());

                                postDataList.add(moodEvent); //add to data list
                        }

                        postAdapter.notifyDataSetChanged();
                    }


                });
    }

    /**
     * This set ups the swipe controller
     */
    private void swipeControllerSetup(){
        final SwipeController swipeController = new SwipeController(new SwipeControllerActions(){
            @Override
            public void onRightClicked(int position){
                Log.d(TAG, "UserFeedActivity - You clicked on the delete button");
                deleteMoodEventFromDB(documentReference, position);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(postList);

        postList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                swipeController.onDraw(c, UserFeedActivity.this);
            }
        });

        postList.addItemDecoration(new DividerItemDecoration(postList.getContext(), DividerItemDecoration.VERTICAL));
    }
    /**
     * This set ups the array adapter for RecyclerView
     */
    private void moodEventAdapterSetup(){
        postDataList = new ArrayList<>();
        postList = findViewById(R.id.posts_list);
        postAdapter = new MoodEventsAdapter(postDataList, this);

        postList.setLayoutManager(new LinearLayoutManager(this));
        postList.setAdapter(postAdapter);
    }

    /**
     * This deletes a MoodEvent from the DB
     * @param documentReference
     *     This is documentReference of MoodEvent in DB
     * @param position
     *     This is the position of MoodEvent in postDataList
     */
    private void deleteMoodEventFromDB(DocumentReference documentReference, int position){
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
     * This is a click listener for create post. Will redirect to CreateEventActivity
     */
    private void createPostBtnClickListener(final String accountName){
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
     * This is a click listener for each MoodEvent that goes to ShowEventActivity
     */
    @Override
    public void onMoodEventClick(int position) {
        Intent intent = new Intent(UserFeedActivity.this, ShowEventActivity.class);
        intent.putExtra(MOOD_EVENT, postDataList.get(position));
        startActivity(intent);
    }
}