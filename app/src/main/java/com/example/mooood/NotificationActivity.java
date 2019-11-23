package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<MoodEvent> Adapter;
    ArrayList<MoodEvent> notificationDataList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference notificationCollectionReference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        Intent intent = getIntent();
        final String name = intent.getStringExtra("accountKey");

        notificationCollectionReference = db.collection("MoodEvents").document(name).collection("request");

        notificationDataList = new ArrayList<>();

        arrayAdapterSetup();


    }

    @Override
    public void onStart() {
        super.onStart();
        notificationCollectionReference
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        notificationDataList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            final String requestNames = documentSnapshot.getId();


                        }
                    }
                });
    }



    private void arrayAdapterSetup () {
        //basic ArrayAdapter init

        listView = findViewById(R.id.notificationListView);
        Adapter = new MoodEventsAdapter(notificationDataList, this);
        listView.setAdapter(Adapter);
    }

}
