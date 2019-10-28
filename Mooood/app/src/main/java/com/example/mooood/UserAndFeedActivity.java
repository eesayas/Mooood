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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserAndFeedActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener {

    private static final String TAG = "UserAndFeedActivity";

    private static final String KEY_DATE = "Date";
    private static final String KEY_TIME = "Time";
    private static final String KEY_EMOTIONAL_STATE = "Emotional State";
    private static final String KEY_REASON = "Reason";
    private static final String KEY_SOCIAL_SITUATION = "social Situation";


    private ListView listView;
    private SearchView searchview;
    private Button feedButton;
    private Button userButton;

    private FloatingActionButton floatingActionButton;
   // private Button floatingActionButton;
    private ArrayAdapter<Mood> moodListAdapter;
    public ArrayList<Mood> MoodList = new ArrayList<Mood>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentRef;
    private CollectionReference collectionRef;
    private Button deleteMoodButton;
    public Mood toDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_and_feed);

        Intent intent = getIntent();
        String accountName = intent.getStringExtra("key");
        documentRef = db.collection("Mood").document(accountName);
        collectionRef = db.collection("Mood");
        deleteMoodButton=findViewById(R.id.activity_main_btn_delete);
        listView = findViewById(R.id.mood_list);
        searchview = findViewById(R.id.search_view);
        feedButton = findViewById(R.id.feedButon);
        userButton = findViewById(R.id.userButton);
        floatingActionButton = findViewById(R.id.floating_button);

        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                MoodList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String accountName = doc.getId();
                    String date = (String) doc.getData().get(KEY_DATE);
                    String time = (String) doc.getData().get(KEY_TIME);
                    String emotionalState = (String) doc.getData().get(KEY_EMOTIONAL_STATE);
                    String reason = (String) doc.getData().get(KEY_REASON);
                    String socialSituation = (String) doc.getData().get(KEY_SOCIAL_SITUATION);
                    MoodList.add(new Mood(date, time, emotionalState, reason, socialSituation));
                }
                moodListAdapter.notifyDataSetChanged();
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");

            }
        });

        moodListAdapter = new MoodListAdapter(this, MoodList);
        listView.setAdapter(moodListAdapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setSelection(position);
                //Show and hide button based on:https://stackoverflow.com/questions/21899825/show-hide-button-when-focus-the-list-item-in-android-listview
                deleteMoodButton.setVisibility(View.VISIBLE);
                view.setSelected(true);
                setToDelete(MoodList.get(position));
                //Calls the delete listener when clicking the delete mood button
                DeleteListener listener= new DeleteListener(getToDelete(), moodListAdapter);
                deleteMoodButton.setOnClickListener(listener);

                return true;
            }

        });
        moodListAdapter = new MoodListAdapter(this, MoodList);
        listView.setAdapter(moodListAdapter);

    }
    public void editMood(Mood mood,String date,String time,String emotionalState,String reason,String socialSituation){
        mood.setTime(time);
        mood.setDate(date);
        mood.setEmotionalState(emotionalState);
        mood.setReason(reason);
        mood.setSocialSituation(socialSituation);
        moodListAdapter.notifyDataSetChanged();
    }


    //Both are interface methods from AddMoodFragment to allow for addition and editing of moods.
    public void addMood(final Mood mood) {
        String date = mood.getDate();
        String time = mood.getTime();
        String emotionalState = mood.getEmotionalState();
        String reason = mood.getReason();
        String socialSituation = mood.getSocialSituation();

        Map<String, String> data = new HashMap<>();
        data.put(KEY_DATE, date);
        data.put(KEY_TIME, time);
        data.put(KEY_EMOTIONAL_STATE, emotionalState);
        data.put(KEY_REASON, reason);
        data.put(KEY_SOCIAL_SITUATION, socialSituation);

        documentRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserAndFeedActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserAndFeedActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });



    }

    //Sets mood to delete
    public void setToDelete(Mood toDelete) {
        this.toDelete = toDelete;
    }
    //Retrieves mood to delete
    public Mood getToDelete() {
        return toDelete;
    }
}


