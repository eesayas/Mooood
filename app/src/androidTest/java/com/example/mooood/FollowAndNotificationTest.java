package com.example.mooood;

import android.app.Activity;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class FollowAndNotificationTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
    /**
     * US 05.01.01
     * US 05.02.01
     * US 05.03.01
     */
    @Test
    public void followUser(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //Signs into Account
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hero");
        solo.waitForText("hero",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        //Clicks on Feed Button
        solo.clickOnView(solo.getView(R.id.feedButton));
        solo.waitForActivity(feedActivity.class);
        SearchView feedSearchView= (SearchView)solo.getView(R.id.feedSearchView);
        feedSearchView.setIconified(false);
        solo.clickOnView(solo.getView(R.id.feedSearchView));
        SearchView searchView = (SearchView) solo.getView(R.id.feedSearchView);

        //Searching up an account in Search View
        int id= searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText= searchView.findViewById(id);
        solo.enterText(editText, "test2");
        solo.waitForText("test2",1,20000);
        solo.sendKey(KeyEvent.KEYCODE_ENTER);

        //Clicks on the moodEvent of the user Searched and clicks on the follow button to send them a request
        solo.clickOnView(solo.getView(R.id.followListView));
        solo.waitForActivity(followerActivity.class);
        solo.clickOnView(solo.getView(R.id.follow_button));

        //Back button to go back to feed activity
        solo.clickOnView(solo.getView(R.id.backButton));
        solo.waitForActivity(followerActivity.class);

        //Goes to User Activity
        solo.clickOnView(solo.getView(R.id.userButton));
        solo.waitForActivity(UserFeedActivity.class);

        //Clicks on profile
        solo.clickOnView(solo.getView(R.id.activity_user_feed_show_profile));
        solo.waitForActivity(UserProfile.class);

        //Clicks on signout
        solo.clickOnView(solo.getView(R.id.activity_user_profile_bt_logout));
        solo.waitForActivity(UserProfile.class);

        //Signs into Account
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "test2");
        solo.waitForText("test2",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "2");
        solo.waitForText("2",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        //Clicks on Feed Button
        solo.clickOnView(solo.getView(R.id.feedButton));
        solo.waitForActivity(feedActivity.class);

        //Goes to notification Activity
        solo.clickOnView(solo.getView(R.id.notificationButton));
        solo.waitForActivity(NotificationActivity.class);

        //Clicks on first list n notifications
        solo.clickInList(0);
        solo.clickOnView(solo.getView(R.id.confirm_button));

        solo.waitForActivity(NotificationActivity.class);
        solo.clickOnView(solo.getView(R.id.backButton1));

        solo.waitForActivity(feedActivity.class);
        solo.clickOnView(solo.getView(R.id.activity_feed_show_profile));

        //Logouts
        solo.clickOnView(solo.getView(R.id.activity_user_profile_bt_logout));
        solo.waitForActivity(UserProfile.class);

        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hero");
        solo.waitForText("hero",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        //Clicks on Feed Button
        solo.clickOnView(solo.getView(R.id.feedButton));
        solo.waitForActivity(feedActivity.class);

        //
        solo.clickOnView(solo.getView(R.id.feedListView));
        TextView moodAuthor = (TextView)solo.getView(R.id.author);
        assertEquals(moodAuthor.getText().toString(),"test2");




    }

    @After
    public void tearDown() throws Exception {
        db.collection("MoodEvents").document("hero").collection("Following").document("test2")
                .delete();
        solo.finishOpenedActivities();


    }

}