package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class followerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_profile);

        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra("accountMood");
    }
}
