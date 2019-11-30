package com.example.mooood;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This test case is for
 *
 * US 06.03.01
 * As a participant, I want to see a map of the mood events (
 * showing their emotional states and the username) from my mood following list (that have locations).
 * So the participant can see where the users that the participant follows were feeling each mood.
 */

@RunWith(AndroidJUnit4.class)
public class FeedMoodMapTest {

    private Solo solo;
    private Calendar calendar;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * This takes user to the feed activity and checks if a user they're following that has a mood event
     * also contains a latitude and longitude both in the feed and in the maps activity when it's called.
     *
     * Tackles:
     *  - US 06.03.01
     */
    @Test
    public void checkLocation(){
        //go to Login
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //enter username and password
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "maxtest");
        solo.waitForText("maxtest",1,2000);

        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);

        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));

        //go to UserFeedActivity (MoodEvent history)
        solo.waitForActivity(UserFeedActivity.class);

        //go to feedActivity
        solo.clickOnView(solo.getView(R.id.feedButton));
        solo.waitForActivity(feedActivity.class);

        //access the RecyclerView where the created MoodEvent should've been added
        feedActivity activity = (feedActivity) solo.getCurrentActivity();

        //TODO: I had to hack this because the onView method was not working.
        //click on first item
        //onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        ArrayList<MoodEvent> feedMoods = activity.getFeedDataList();
        solo.clickOnView(solo.getView(R.id.map_feed_button));
        solo.clickOnImageButton(0);
        solo.waitForActivity(MoodsMapActivity.class);
        MoodsMapActivity mapActivity = (MoodsMapActivity) solo.getCurrentActivity();
        ArrayList<MoodEvent> mapMoods = mapActivity.getMoodsList();

        assertEquals(mapMoods.get(0).getLatitude(),feedMoods.get(0).getLatitude());

        solo.sleep(2000); //for visual
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
