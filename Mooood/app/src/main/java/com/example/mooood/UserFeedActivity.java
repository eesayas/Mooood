package com.example.mooood;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Arrays;

public class UserFeedActivity extends AppCompatActivity{

    //Declare the variables for reference later
    SwipeMenuListView postList;
    ArrayAdapter<String> postAdapter; //<String> for now
    ArrayList<String> postDataList; //<String> for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        //temporary data for ListView
        String[] moods = {"Happy", "Sad"};
        postDataList = new ArrayList<>();
        postDataList.addAll(Arrays.asList(moods));

        //basic ArrayAdapter init
        postList = findViewById(R.id.posts_list);
        postAdapter = new ArrayAdapter<>(this, R.layout.content_user_feed, postDataList);
        postList.setAdapter(postAdapter);

        //adding delete button to SwipeMenu
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                //init delete button
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());

                //custom design for delete button
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_delete_forever);

                //add button
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        postList.setMenuCreator(creator);

        //go to CreatePostActivity
        final FloatingActionButton createPostBtn = findViewById(R.id.fab);
        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                startActivity(intent);

            }
        });

    } //end of onCreate

}