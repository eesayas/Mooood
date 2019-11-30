package com.example.mooood;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.UiSettings;
import com.robotium.solo.Solo;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static androidx.test.espresso.Espresso.onView;
import static org.junit.Assert.assertNotEquals;

/**
 * This test case is for
 *
 *  US 01.05.01
 *  As a participant, I want to delete a given mood event of mine.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteMoodEventTest{
    private Solo solo;
    private Calendar calendar;

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
        //Sign in to the app
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "eesayas");
        solo.waitForText("eesayas",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "lol");
        solo.waitForText("lol",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);


        //======================================================
        // SAD MOODEVENT
        //======================================================

        //go to CreateEventActivity
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity(CreateEventActivity.class);

        //choose SAD emoticon
        onView(withId(R.id.mood_roster)).perform(swipeLeft());
        solo.clickOnView(solo.getView(R.id.select_emoticon_btn));
        solo.clickOnView(solo.getView(R.id.submit_button));

        //should be redirected to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class);

        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Make sure SAD was added
        solo.waitForActivity(ShowEventActivity.class);
        ImageView emotionHappy = (ImageView) solo.getView(R.id.emoticon);
        assertEquals(emotionHappy.getTag(), new Emoticon("SAD", 2).getImageLink());
        solo.clickOnView(solo.getView(R.id.back_btn));
        solo.waitForActivity(UserFeedActivity.class);

        //======================================================
        // HAPPY MOODEVENT (will be deleted)
        //======================================================
        //go to CreateEventActivity
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity(CreateEventActivity.class);

        //HAPPY by default
        solo.clickOnView(solo.getView(R.id.select_emoticon_btn));

        //click submit button to create minimal MoodEvent
        solo.clickOnView(solo.getView(R.id.submit_button));

        //should be redirected to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class);

        solo.sleep(5000);
        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Make sure HAPPY was added
        solo.waitForActivity(ShowEventActivity.class);
        ImageView emotionSad = (ImageView) solo.getView(R.id.emoticon);
        assertEquals(emotionSad.getTag(), new Emoticon("HAPPY", 2).getImageLink());
        solo.clickOnView(solo.getView(R.id.back_btn));

        //should be redirected back to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class);

        //swipe first item
        solo.scrollToTop();
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));

        //click on "garbage" button to delete MoodEvent
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickOnDeleteBtn()));

        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //go to the ShowEventActivity of the choosen MoodEvent
        solo.waitForActivity(ShowEventActivity.class);

        //get ImageView of emoticon
        emotionSad = (ImageView) solo.getView(R.id.emoticon);

        //make sure prior item was successfully deleted because this is on top now
        assertEquals(emotionSad.getTag(), new Emoticon("SAD", 2).getImageLink());
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * This is for clicking the child view (aka the delete btn) of the selected MoodEvent
     */
    public class ClickOnDeleteBtn implements ViewAction {

        ViewAction click = click();

        @Override
        public Matcher<View> getConstraints() {
            return null;
        }

        @Override
        public String getDescription() {
            return "Click on a child view with specified id.";
        }

        @Override
        public void perform(UiController uiController, View view) {
            click.perform(uiController, view.findViewById(R.id.menu));
        }
    }
}