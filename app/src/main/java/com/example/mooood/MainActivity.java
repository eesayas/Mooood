package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is temporary
        // Prototype 1 of the app will be focused on add and delete
        final Button goToFeedBtn = findViewById(R.id.activity_main_btn_submit);
        goToFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                startActivity(intent);

            }
        });

    }

}
