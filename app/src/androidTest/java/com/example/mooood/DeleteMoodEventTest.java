package com.example.mooood;

import android.app.Activity;
import android.widget.EditText;

import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.baoyz.swipemenulistview.SwipeMenuAdapter;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


import static androidx.test.espresso.Espresso.onView;


/**
 * This is a test class for deleting a MoodEvent in UserFeedActivity (for US 01.05.01)
 **/
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteMoodEventTest{
    private Solo solo;
    Calendar calendar;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance
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
     * Swipes the selected view (i.e. content_user_feed)
     * Clicks on delete "garbage" button
     * Checks if selected MoodEvent is deleted from UserFeedActivity and DB
     */
    @Test
    public void checkDelete(){
        //Sign in to the app first
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "eesayas");
        solo.waitForText("eesayas",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "lol");
        solo.waitForText("lol",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        //Create a most recent MoodEvent (Assumption: This MoodEvent will be at the top)
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity(CreateEventActivity.class);

        //Temp solution. Must find a way to access DialogPicker
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");

        solo.clickOnView(solo.getView(R.id.submit_button));

        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class);

        //swipe first item
        onData(anything()).inAdapterView(withId(R.id.posts_list)).atPosition(0).perform(swipeLeft());


        //Assert that the current activity is the ShowActivity
//        solo.assertCurrentActivity("Wrong Activity", ShowEventActivity.class);

    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}