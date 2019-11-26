package com.example.mooood;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ShowEventActivityTest {
    private Solo solo;
    public static Calendar calendar;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

//    public void ListViewClickTest () {
//
//        super (ShowEventActivity.class);
//    }
    @Test
    public void showActivityLaunch(){

        //Log In using a known user ID
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        //Create mood event with: happy (default emoji), this is a reason, alone, current date and time, no picture for now, no location for now.
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity(CreateEventActivity.class);
        solo.enterText((EditText) solo.getView(R.id.reason),"This");
        solo.clickOnView(solo.getView(R.id.social_situation));
        solo.clickOnText("Alone");
        solo.clickOnView(solo.getView(R.id.date_and_time));
//        solo.waitForFragmentByTag("MyTag");
        solo.waitForText("OK",1,2000);
        solo.clickOnText("OK");
        solo.waitForText("OK",1,2000);
//        EditText date = (EditText)solo.getView(R.id.date_and_time);
//        String date_time = date.getText().toString();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("MMM dd yyyy");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("h:mm a");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf1.format(now);
        String time = dtf2.format(now);

        solo.clickOnText("OK");              //get the date and time
        calendar = Calendar.getInstance();
        solo.clickOnView(solo.getView(R.id.submit_button));
        solo.waitForActivity(UserFeedActivity.class);

        //click on the mood created to start the ShowEvenetActivity
        final ListView list = (ListView) solo.getView(R.id.posts_list);
        solo.clickInList(0);
        solo.waitForActivity(ShowEventActivity.class);
        TextView moodAuthor = (TextView)solo.getView(R.id.author);
        TextView moodDate = (TextView)solo.getView(R.id.date);
        TextView moodTime = (TextView)solo.getView(R.id.time);
        TextView moodSocialSituation = (TextView)solo.getView(R.id.social_situation);
        TextView moodReason =(TextView)solo.getView(R.id.reason);
        assertEquals(moodAuthor.getText().toString(),"hyeon");
        assertEquals(moodSocialSituation.getText().toString(),"Alone");
        assertEquals(moodReason.getText().toString(),"This");
        assertEquals(moodDate.getText().toString(), date);
        assertEquals(moodTime.getText().toString(), time);
    }



    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}