package com.example.mooood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserProfile extends AppCompatActivity {
    private ImageView profilePic, recent;
    private TextView userName, recentDate, recentTime;

    /**
     * This sets up the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profilePic = findViewById(R.id.activity_user_profile_iv_pic);
        userName = findViewById(R.id.activity_user_profile_tv_usernam);
        recent = findViewById(R.id.activity_user_profileiv_recent);
        recentDate = findViewById(R.id.activity_user_profile_tv_date);
        recentTime = findViewById(R.id.activity_user_profile_time);

        Bundle bundle = getIntent().getExtras();
        String author =bundle.getString("AUTHOR");

        String userDate=bundle.getString("DATE");
        String userTime=bundle.getString("TIME");
        String userState=bundle.getString("STATE");
        String userImage=bundle.getString("IMAGE");

        userName.setText(author);
        recentTime.setText(userTime);
        recentDate.setText(userDate);
        recent.setImageResource(new Emoticon(userState, 2).getImageLink());

    }

    /**
     * takes the user back to where they came from
     */
    public void backToFeed(View view) {
        finish();
    }


    /**
     * logs out the user and sends the user back to mainactivity
     */
    public void logout(View view) {

        finishAffinity();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
