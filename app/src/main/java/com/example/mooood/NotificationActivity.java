package com.example.mooood;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private ListView listView;
    private ArrayAdapter<Notification> Adapter;
    private ArrayList<Notification> notificationDataList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userName, requestName, requestName2;
    int position;
    private CollectionReference notificationCollectionReference;
    private Button backButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Log.d("notification activity","inside the onCreate");

        Intent intent = getIntent();
        userName = intent.getStringExtra("accountKey");
        Log.d("notification activity","account name: "+ userName);

        notificationCollectionReference = db.collection("MoodEvents").document(userName).collection("Request");

        notificationDataList = new ArrayList<>();

        arrayAdapterSetup();
        showNotification();

        backButton = findViewById(R.id.backButton1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * gets the follow request from db adn adds it to the notificationDataList
     */
    @Override
    protected void onStart() {
        super.onStart();
        notificationDataList.clear();
        notificationCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    requestName = (String) queryDocumentSnapshot.getData().get("Username");
                    String time= (String) queryDocumentSnapshot.getData().get("Request Time");
                    Log.d("namefollow ", requestName);
                    Log.d("timefollow ", time);
                    Notification notification = new Notification(requestName, time);

                    if (notificationDataList.contains(notification)){
                        Log.d("duplicates", "Already exist in the list   " + notification.getUsername());
                    }
                    else {
                        Log.d("duplicates", "added in the list " + notification.getUsername());
                        notificationDataList.add(notification);
                    }
                }
                Adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * sets up the adapter according to the notification adapter with the notificationDataList
     */
    private void arrayAdapterSetup () {
        //basic ArrayAdapter init

        listView = findViewById(R.id.notificationListView);
        Adapter = new NotificationAdapter(notificationDataList, this);
        listView.setAdapter(Adapter);
    }
    /**
     * This is a click listener for show notification. Will redirect to notification fragment
     */
    private void showNotification () {
        //click listener for each item -> ShowEventActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                requestName2 = notificationDataList.get(i).getUsername();
                position = i;
                new ShowNotificationFragment().show(getSupportFragmentManager(), "Show Notification");
//                notificationDataList.remove(i);
//                Adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * gets the username in the follow request to be used in the fragment
     */
    public String getRequestName() {
        return requestName2;
    }

    public String getUserName(){
        return userName;
    }

    public void notifydata(){
        Log.d("notify_data", "in notifydata and pos: "+ position);
        notificationDataList.remove(position);
        Adapter.notifyDataSetChanged();
    }



}