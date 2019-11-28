package com.example.mooood;

import android.app.Activity;
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

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class SearchForUsersTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);


    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
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

    @Test
    public void searchForUsers(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "maaz");
        solo.waitForText("maaz1",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);
        solo.clickOnView(solo.getView(R.id.feedButton));
        solo.waitForActivity(feedActivity.class);
        SearchView feedSearchView= (SearchView)solo.getView(R.id.feedSearchView);
        feedSearchView.setIconified(false);
        solo.clickOnView(solo.getView(R.id.feedSearchView));
        SearchView searchView = (SearchView) solo.getView(R.id.feedSearchView);
        int id= searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText= searchView.findViewById(id);
        solo.enterText(editText, "madmax");
        solo.waitForText("madmax",1,20000);
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
        solo.clickOnView(solo.getView(R.id.followListView));
        solo.waitForActivity(followerActivity.class);
        TextView moodAuthor = (TextView)solo.getView(R.id.author);
        assertEquals(moodAuthor.getText().toString(),"madmax");





    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

