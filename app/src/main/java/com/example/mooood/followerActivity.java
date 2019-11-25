package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Show profile of searched user and can send request
 */
public class followerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_profile);

        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra("accountMood");

        // Set up display
    }


    public void requestedOrCancel(View view) {
        // change button into requested if clicked again cancel the request

    }


    public void backToSearch(View view) {
//        figure out a way to go back with filled listview
//        Intent intent = new Intent(followerActivity.this, feedActivity.class );
//        startActivity(intent);
        finish();
//        ?????????
    }
}
